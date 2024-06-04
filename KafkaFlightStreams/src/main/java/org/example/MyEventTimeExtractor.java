package org.example;

import org.example.models.MovieRating;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import java.time.LocalDate;

public class MyEventTimeExtractor implements TimestampExtractor {

    public long extract(final ConsumerRecord<Object, Object> consumerRecord, long previousTimestamp) {
        long timestamp = -1;
        String line;

        if (consumerRecord.value() instanceof String) {
            line = (String) consumerRecord.value();

            if (MovieRating.lineIsCorrect(line)) {
                timestamp = MovieRating.parseFromLogLine(line).extractTimestamp();
            } else {
                timestamp = System.currentTimeMillis();
            }
        }
        return timestamp;
    }
}
