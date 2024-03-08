package com.example.trackingservice.service;

import com.example.dispatch.message.DispatchPreparing;
import com.example.trackingservice.message.TrackingStatus;
import com.example.trackingservice.message.TrackingStatusUpdated;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {
  private static final String TRACKING_STATUS_TOPIC = "tracking.status";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Override
  public void updateTrackingStatus(UUID orderId, TrackingStatus status) throws Exception {
    var trackingStatusUpdated =
        TrackingStatusUpdated.builder()
            .orderId(orderId)
            .status(status)
            .build();
    kafkaTemplate.send(TRACKING_STATUS_TOPIC, trackingStatusUpdated).get();
  }
}
