package pl.daveproject;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemo {
  private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class);
  private static final String KAFKA_HOST = "127.0.0.1";
  private static final String KAFKA_PORT = "9092";
  private static final String KAFKA_TOPIC_NAME = "demo_java";
  private static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
  private static final String KEY_DESERIALIZER = "key.deserializer";
  private static final String VALUE_DESERIALIZER = "value.deserializer";
  private static final String GROUP_ID_KEY = "group.id";
  private static final String GROUP_ID_VALUE = "java-kafka-tutorial";
  private static final String AUTO_OFFSET_RESET_KEY = "auto.offset.reset";

  // possible values: none, earliest, latest
  private static final String AUTO_OFFSET_RESET_VALUE = "earliest";

  public static void main(String[] args) {
    log.info("Creating consumer properties");
    var properties = new Properties();
    properties.setProperty(BOOTSTRAP_SERVERS, "%s:%s".formatted(KAFKA_HOST, KAFKA_PORT));
    properties.setProperty(KEY_DESERIALIZER, StringDeserializer.class.getName());
    properties.setProperty(VALUE_DESERIALIZER, StringDeserializer.class.getName());
    properties.setProperty(GROUP_ID_KEY, GROUP_ID_VALUE);
    properties.setProperty(AUTO_OFFSET_RESET_KEY, AUTO_OFFSET_RESET_VALUE);

    log.info("Create the consumer");
    var kafkaConsumer = new KafkaConsumer<String, String>(properties);
    try {
      setupShutdownHook(kafkaConsumer);
      log.info("Subscribe to topic");
      kafkaConsumer.subscribe(List.of(KAFKA_TOPIC_NAME));

      log.info("Pull data from topic...");
      while (true) {
        var consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
        for (var record : consumerRecords) {
          log.info(
              "Key: {}, Value: {}, Partition: {}, Offset: {}",
              record.key(),
              record.value(),
              record.partition(),
              record.offset());
        }
      }
    } catch (WakeupException e) {
      log.info("Starting shut down the Kafka consumer...");
    } catch (Exception e) {
      log.error("Unexpected exception", e);
    } finally {
      kafkaConsumer.close();
      log.info("Kafka consumer shut down successfully");
    }
  }

  private static void setupShutdownHook(KafkaConsumer<String, String> kafkaConsumer) {
    final var mainThread = Thread.currentThread();

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  log.info("Detected shutdown, let's exit by calling wakeup()...");
                  kafkaConsumer.wakeup();
                  try {
                    mainThread.join();
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  }
                }));
  }
}
