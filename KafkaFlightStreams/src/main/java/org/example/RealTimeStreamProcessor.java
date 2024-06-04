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

        String movieRatingInputTopic = "netflix-ratings-input";
        String movieInfoInputTopic = "movie-info-input";
        String ELTOutputTopic = "etl-output";

        StreamsBuilder builder = new StreamsBuilder();

        // = AIRPORTS = MovieInfo
        //KTable<String, MovieInfo> movieInfoTable = getMovieInfoTable(movieInfoSerde, movieInfoInputTopic, builder);
//        movieInfoTable.toStream().foreach((key, value) -> System.out.println("SYSTEMOUT: " + key + ": " + value.toString()));

        // = FLIGHTS = MovieRating
        KStream<String, MovieRating> movieRating = getMovieRating(movieRatingInputTopic, builder);
        KStream<String, MovieRating> movieInfoID = movieRating.selectKey((key, value) -> value.getFilm_id());

        // = ETL =
        KTable<Windowed<String>, MovieAggregate> moviesETL = getETLData(movieRatingSerde, movieAggregateSerde, movieRating, delay);


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
            Serde<MovieRating> movieRatingSerde,
            Serde<MovieAggregate> movieAggregateSerde,
            KStream<String, MovieRating> movieRating,
            String delay) {

        KTable<Windowed<String>, MovieAggregate> movieAggregates = movieRating
                .selectKey((key, value) -> MovieRating.parseOrderColumns(value.getDate()))
                .groupByKey(Grouped.with(Serdes.String(), movieRatingSerde))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofDays(1)))
                .aggregate(
                        MovieAggregate::new,
                        (key, value, aggregate) -> {
                            aggregate.setRatingAmount(aggregate.getRatingAmount() + 1);
                            aggregate.setRatingSum(aggregate.getRatingSum() + Integer.parseInt(value.getRate()));

//                            Set<String> updatedUniqueUsers = new HashSet<>(aggregate.getUniqueUsers());
//                            updatedUniqueUsers.add(value.getUser_id());
//                            aggregate.setUniqueUsers(updatedUniqueUsers);

                            return aggregate;
                        },
                        Materialized.with(Serdes.String(), movieAggregateSerde)
                );
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
                .selectKey((key, value) -> value.getTitle())
                .toTable(Materialized.with(Serdes.String(), movieInfoSerde));
    }
    //    private static Map<String, MovieInfo> loadMovieTitles(String path) throws Exception {
//        Map<String, MovieInfo> movieTitlesMap = new HashMap<>();
//        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
//            reader.readLine(); // Skip the header line
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(",");
//                if (parts.length >= 3) {
//                    String id = parts[0];
//                    String title = parts[2];
//                    int year;
//                    if ("NULL".equals(parts[1])) {
//                        year = 0; // Assigning a default value (you can change it as needed)
//                    } else {
//                        year = Integer.parseInt(parts[1]);
//                    }
//                    movieTitlesMap.put(id, new MovieInfo(id, title, year));
//                }
//            }
//        }
//        return movieTitlesMap;
//    }
}
