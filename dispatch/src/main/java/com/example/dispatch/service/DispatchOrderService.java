package com.example.dispatch.service;

import com.example.dispatch.message.DispatchCompleted;
import com.example.dispatch.message.DispatchPreparing;
import com.example.dispatch.message.OrderCreated;
import com.example.dispatch.message.OrderDispatched;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchOrderService {
  private static final String ORDER_DISPATCHED_TOPIC = "order.dispatched";
  private static final String DISPATCH_TRACKING_TOPIC = "dispatch.tracking";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void process(OrderCreated payload) throws Exception {
    var orderDispatched = OrderDispatched.builder().orderId(payload.getOrderId()).build();
    kafkaTemplate.send(ORDER_DISPATCHED_TOPIC, orderDispatched).get();

    var dispatchPrepared = DispatchPreparing.builder().orderId(payload.getOrderId()).build();
    kafkaTemplate.send(DISPATCH_TRACKING_TOPIC, dispatchPrepared).get();

    var dispatchCompleted =
        DispatchCompleted.builder()
            .date(OffsetDateTime.now().toString())
            .orderId(payload.getOrderId())
            .build();
    kafkaTemplate.send(DISPATCH_TRACKING_TOPIC, dispatchCompleted);
  }
}
