package com.example.trackingservice.message;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackingStatusUpdated {
  private UUID orderId;
  private TrackingStatus status;
}
