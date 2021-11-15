package scalable.solutions.kafka;

public class KafkaConstants {
    public static final String KAFKA_BROKERS = "localhost:9092";

    public static final long MESSAGE_COUNT = 1000L;

    public static final String CLIENT_ID = "kafka_redis";

    public static final String TOPIC_NAME = "redis-events";

    public static final String GROUP_ID_CONFIG = "redis";

    public static final int MAX_NO_MESSAGE_FOUND_COUNT = 100;

    public static final String OFFSET_RESET_LATEST = "latest";

    public static final String OFFSET_RESET_EARLIER = "earliest";

    public static final int MAX_POLL_RECORDS = 1;

    private KafkaConstants() {
        // -- ctor
    }
}