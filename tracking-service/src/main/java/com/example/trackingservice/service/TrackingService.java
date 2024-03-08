package com.example.trackingservice.service;

import com.example.trackingservice.message.TrackingStatus;
import java.util.UUID;

public interface TrackingService {
  void updateTrackingStatus(UUID orderId, TrackingStatus status) throws Exception;
}
