package com.example.trackingservice.handler;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.dispatch.message.DispatchPreparing;
import com.example.trackingservice.message.TrackingStatus;
import com.example.trackingservice.service.TrackingService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DispatchPreparingHandlerTest {
  private TrackingService trackingService;
  private DispatchPreparingHandler dispatchPreparingHandler;

  @BeforeEach
  void beforeEach() {
    trackingService = mock(TrackingService.class);
    dispatchPreparingHandler = new DispatchPreparingHandler(trackingService);
  }

  @Test
  void listen() throws Exception {
    var dispatchPreparing = new DispatchPreparing(UUID.randomUUID());
    dispatchPreparingHandler.prepareDispatch(dispatchPreparing);
    verify(trackingService)
        .updateTrackingStatus(eq(dispatchPreparing.getOrderId()), eq(TrackingStatus.PREPARING));
  }
}
