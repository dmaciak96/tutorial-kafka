package pl.daveproject;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemo {
  private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);
  private static final String KAFKA_HOST = "127.0.0.1";
  private static final String KAFKA_PORT = "9092";
  private static final String KAFKA_TOPIC_NAME = "demo_java";
  private static final String KEY_SERIALIZER = "key.serializer";
  private static final String VALUE_SERIALIZER = "value.serializer";
  private static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
  private static final String KAFKA_MESSAGE_VALUE = "Hello World";
  private static final String KAFKA_MESSAGE_KEY = "FIRST_MESSAGE";

  public static void main(String[] args) {
    log.info("Creating Producer properties");
    var properties = new Properties();
    properties.setProperty(BOOTSTRAP_SERVERS, "%s:%s".formatted(KAFKA_HOST, KAFKA_PORT));
    properties.setProperty(KEY_SERIALIZER, StringSerializer.class.getName());
    properties.setProperty(VALUE_SERIALIZER, StringSerializer.class.getName());

    log.info("Create the producer");
    var kafkaProducer = new KafkaProducer<String, String>(properties);
    var producerRecord =
        new ProducerRecord<>(KAFKA_TOPIC_NAME, KAFKA_MESSAGE_KEY, KAFKA_MESSAGE_VALUE);

    kafkaProducer.send(
        producerRecord,
        (recordMetadata, exception) -> {
          // This will be invoked every time when a record successfully sent or an exception is
          // thrown
          if (exception == null) {
            log.info(
                "Received new metadata \n [Topic: {}, Partition: {}, Offset: {}, Timestamp: {}]",
                recordMetadata.topic(),
                recordMetadata.partition(),
                recordMetadata.offset(),
                recordMetadata.timestamp());
          } else {
            log.error("Error during message producing: ", exception);
          }
        });

    log.info("Flush and close the producer");
    kafkaProducer.close();
  }
}
