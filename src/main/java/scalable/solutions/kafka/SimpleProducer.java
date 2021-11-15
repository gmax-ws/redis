package scalable.solutions.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SimpleProducer {

    private static final Logger logger = LoggerFactory.getLogger(SimpleProducer.class);

    private SimpleProducer() {
    }

    private static Properties config() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConstants.CLIENT_ID);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        return props;
    }

    public static void main(String[] args) {
        try (Producer<Integer, String> kafkaProducer = new KafkaProducer<>(config())) {

            for (int i = 0; i < 10; i++) {
                String topic = KafkaConstants.TOPIC_NAME;
                ProducerRecord<Integer, String> record = new ProducerRecord<>(topic, i, "message_" + i);

                RecordMetadata m = kafkaProducer.send(record).get();
                logger.info("Message produced, offset: {} partition : {} topic: {}",
                        m.offset(), m.partition(), m.topic());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
