package com.example.trackingservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.trackingservice.message.TrackingStatus;
import com.example.trackingservice.message.TrackingStatusUpdated;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

class TrackingServiceImplTest {
  private static final String TRACKING_STATUS_TOPIC = "tracking.status";
  private static final String ERROR_MESSAGE = "Processing Failure";

  private TrackingServiceImpl trackingService;
  private KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeEach
  void setUp() {
    kafkaTemplate = mock(KafkaTemplate.class);
    trackingService = new TrackingServiceImpl(kafkaTemplate);
  }

  @Test
  void updateTrackingStatusSuccessfully() throws Exception {
    when(kafkaTemplate.send(anyString(), any(TrackingStatusUpdated.class)))
        .thenReturn(mock(CompletableFuture.class));

    trackingService.updateTrackingStatus(UUID.randomUUID(), TrackingStatus.PREPARING);
    verify(kafkaTemplate).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
  }

  @Test
  void updateTrackingStatusFailed() {
    doThrow(new RuntimeException(ERROR_MESSAGE))
        .when(kafkaTemplate)
        .send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));

    var resultException =
        assertThrows(
            RuntimeException.class,
            () ->
                trackingService.updateTrackingStatus(UUID.randomUUID(), TrackingStatus.PREPARING));
    assertThat(resultException.getMessage()).isEqualTo(ERROR_MESSAGE);
    verify(kafkaTemplate).send(eq(TRACKING_STATUS_TOPIC), any(TrackingStatusUpdated.class));
  }
}
