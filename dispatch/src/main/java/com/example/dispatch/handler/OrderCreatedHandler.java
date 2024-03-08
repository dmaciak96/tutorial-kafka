package com.example.dispatch.handler;

import com.example.dispatch.message.OrderCreated;
import com.example.dispatch.service.DispatchOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedHandler {

  private final DispatchOrderService dispatchOrderService;

  @KafkaListener(
      id = "orderConsumerClient",
      topics = "order.created",
      groupId = "dispatch.order.created.consumer",
      containerFactory = "kafkaListenerContainerFactory")
  public void listen(OrderCreated orderCreated) {
    log.info("Incoming event {}", orderCreated);
    try {
      dispatchOrderService.process(orderCreated);
    } catch (Exception e) {
      log.error("Processing failure", e);
    }
  }
}
