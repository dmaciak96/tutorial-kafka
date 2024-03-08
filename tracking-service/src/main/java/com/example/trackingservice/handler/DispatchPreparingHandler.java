package com.example.trackingservice.handler;

import com.example.dispatch.message.DispatchCompleted;
import com.example.dispatch.message.DispatchPreparing;
import com.example.trackingservice.message.TrackingStatus;
import com.example.trackingservice.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
    id = "trackingConsumerClient",
    topics = "dispatch.tracking",
    groupId = "tracking.dispatch.consumer",
    containerFactory = "kafkaListenerContainerFactory")
public class DispatchPreparingHandler {

  private final TrackingService trackingService;

  @KafkaHandler
  public void prepareDispatch(DispatchPreparing dispatchPreparing) {
    log.info("Incoming DispatchPreparing event {}", dispatchPreparing);
    try {
      trackingService.updateTrackingStatus(
          dispatchPreparing.getOrderId(), TrackingStatus.PREPARING);
    } catch (Exception e) {
      log.error("Dispatch prepare failure", e);
    }
  }

  @KafkaHandler
  public void completeDispatch(DispatchCompleted dispatchCompleted) {
    log.info("Incoming DispatchCompleted event {}", dispatchCompleted);
    try {
      trackingService.updateTrackingStatus(
          dispatchCompleted.getOrderId(), TrackingStatus.COMPLETED);
    } catch (Exception e) {
      log.error("Dispatch complete failure", e);
    }
  }
}
