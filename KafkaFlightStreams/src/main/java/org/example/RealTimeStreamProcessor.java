package org.example;

import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.common.serialization.Serde;
import org.example.models.*;
import org.example.serialization.GenericSerde;

import java.time.Duration;
import java.util.Map;
import java.io.FileReader;
import java.util.*;
import java.io.BufferedReader;
import java.util.concurrent.CountDownLatch;

public class RealTimeStreamProcessor {

    public static void main(String[] args) throws Exception {
        String bootstrapServers = args[0];
        int D = Integer.parseInt(args[1]);
        int L = Integer.parseInt(args[2]);
        int O = Integer.parseInt(args[3]);
        String delay = args[4];

        System.out.println("=== PARAMS ===");
        System.out.println("D: " + D + ", L: " + L + ", O: " + O + ", delay: " + delay + ", server: " + bootstrapServers);

        Properties config = new Properties();
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "real-time-stream-processor");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, MyEventTimeExtractor.class.getName());

        Serde<MovieRating> movieRatingSerde = new GenericSerde<>(MovieRating.class);
        Serde<MovieInfo> movieInfoSerde = new GenericSerde<>(MovieInfo.class);
        Serde<MovieAggregate> movieAggregateSerde = new GenericSerde<>(MovieAggregate.class);
        Serde<MovieRatingInfoJoined> movieRatingInfoJoinedSerde = new GenericSerde<>(MovieRatingInfoJoined.class);

        String movieRatingInputTopic = "netflix-ratings-input";
        String movieInfoInputTopic = "movie-info-input";
        String ELTOutputTopic = "etl-output";

        StreamsBuilder builder = new StreamsBuilder();

        // MovieInfo
        KTable<String, MovieInfo> movieInfoTable = getMovieInfoTable(movieInfoSerde, movieInfoInputTopic, builder);

        // MovieRating
        KStream<String, MovieRating> movieRating = getMovieRating(movieRatingInputTopic, builder);
        KStream<String, MovieRating> movieRatingID = movieRating.selectKey((key, value) -> value.getFilm_id());

        KStream<String, MovieRatingInfoJoined> joined = movieRatingID
                .join(movieInfoTable, (MovieRating val1, MovieInfo val2) -> {
                            try {
                                return new MovieRatingInfoJoined(val1.dateString, val1.film_id, val1.user_id, val1.rate, val2.getTitle());
                            } catch (Exception e) {
                                System.out.println("join error");
                                System.out.println(e.getMessage());
                                System.out.println(e.getClass());
                                return new MovieRatingInfoJoined();
                            }
                        }
                        , Joined.with(Serdes.String(), movieRatingSerde, movieInfoSerde));

        //joined.foreach((key, value) -> System.out.println("joinedLines: " + key + ": " + value.toString()));

        // ETL
        KTable<Windowed<String>, MovieAggregate> moviesETL = getETLData(movieRatingInfoJoinedSerde, movieAggregateSerde, joined, delay);


//        moviesETL.toStream()
//                .selectKey((windowedKey, value) -> windowedKey.key())
//                .to(ELTOutputTopic);

        moviesETL.toStream()
                .selectKey((windowedKey, value) -> windowedKey.key())
                .foreach((key, value) -> System.out.println("SYSTEMOUT: " + key + ": " + value.toString()));


        final Topology topology = builder.build();
        //System.out.println(topology.describe());
        KafkaStreams streams = new KafkaStreams(topology, config);
        final CountDownLatch latch = new CountDownLatch(1);

        streams.setUncaughtExceptionHandler((thread, exception) -> {
            System.out.println(exception.getMessage());
        });

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    private static KTable<Windowed<String>, MovieAggregate> getETLData(
            Serde<MovieRatingInfoJoined> movieRatingInfoJoinedSerde,
            Serde<MovieAggregate> movieAggregateSerde,
            KStream<String, MovieRatingInfoJoined> joined,
            String delay) {

        KTable<Windowed<String>, MovieAggregate> movieAggregates = null;
        try {
            movieAggregates = joined
                    .groupByKey(Grouped.with(Serdes.String(), movieRatingInfoJoinedSerde))
                    .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(1)))
                    .aggregate(
                            MovieAggregate::new,
                            (key, value, aggregate) -> {
                                aggregate.setTitle(value.getTitle());

                                aggregate.setRatingAmount(aggregate.getRatingAmount() + 1);
                                aggregate.setRatingSum(aggregate.getRatingSum() + Integer.parseInt(value.getRate()));

                                Set<String> updatedUniqueUsers = new HashSet<>(aggregate.getUniqueUsers());
                                updatedUniqueUsers.add(value.getUser_id());
                                aggregate.setUniqueUsers(updatedUniqueUsers);

                                aggregate.setUniqueUsersCount(aggregate.getUniqueUsers().size());

                                return aggregate;
                            },
                            Materialized.with(Serdes.String(), movieAggregateSerde)
                    );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while building ETL data.", e);
        }

        if (delay.equals("C")) {
            return movieAggregates.suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()));
        }
        return movieAggregates;
    }

    private static KStream<String, MovieRating> getMovieRating(String movieRatingInputTopic, StreamsBuilder builder) {
        return builder
                .stream(movieRatingInputTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .filter((key, value) -> MovieRating.isLineCorrect(value))
                .mapValues(MovieRating::parseFromLine);
    }

    private static KTable<String, MovieInfo> getMovieInfoTable(Serde<MovieInfo> movieInfoSerde, String movieInfoInputTopic, StreamsBuilder builder) {
        return builder
                .stream(movieInfoInputTopic, Consumed.with(Serdes.String(), Serdes.String()))
                .filter((key, line) -> MovieInfo.isLineCorrect(line))
                .mapValues(MovieInfo::parseFromLine)
                .map((key, value) -> KeyValue.pair(value.getID(), value))
                .toTable(Materialized.with(Serdes.String(), movieInfoSerde));
    }
    private static Map<String, MovieInfo> loadMovieTitles(String path) throws Exception {
        Map<String, MovieInfo> movieTitlesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.readLine(); // Skip the header line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String id = parts[0];
                    String title = parts[2];
                    String year;
                    if ("NULL".equals(parts[1])) {
                        year = "0"; // Assigning a default value (you can change it as needed)
                    } else {
                        year = parts[1];
                    }
                    movieTitlesMap.put(id, new MovieInfo(id, title, year));
                }
            }
        }
        return movieTitlesMap;
    }
}
