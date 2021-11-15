package scalable.solutions.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SimpleConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);

    private SimpleConsumer() {
    }

    private static Properties config() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID_CONFIG);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, KafkaConstants.MAX_POLL_RECORDS);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaConstants.OFFSET_RESET_EARLIER);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        return props;
    }

    private static void subscribe(KafkaConsumer<Integer, String> consumer, long startOffset) {
        if (startOffset == -1L) {
            consumer.subscribe(Collections.singletonList(KafkaConstants.TOPIC_NAME));
        } else {
            TopicPartition tp = new TopicPartition(KafkaConstants.TOPIC_NAME, 0);
            List<TopicPartition> topics = List.of(tp);
            consumer.assign(topics);
            consumer.seek(tp, startOffset);
        }
    }

    public static void main(String[] args) {

        RedisCache cache = RedisCache.create("redis://localhost?timeout=60s&_database=kafka");

        try (KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(config())) {
            subscribe(consumer, -1L);

            while (true) {
                ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<Integer, String> record : records) {
                    if (cache.isNotDuplicateKey(record.key(), record.value(), 10L)) {
                        logger.info("Consumed message offset = {}, key = {}, value = {}",
                                record.offset(), record.key(), record.value());
                        // do something
                    } else {
                        logger.warn("Duplicate key {}", record.key());
                    }
                }
            }
        }
    }
}
