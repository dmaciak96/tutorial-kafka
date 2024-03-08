package com.example.dispatch.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.dispatch.message.DispatchPreparing;
import com.example.dispatch.message.OrderCreated;
import com.example.dispatch.message.OrderDispatched;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

class DispatchOrderServiceTest {

  private DispatchOrderService dispatchOrderService;
  private KafkaTemplate<String, Object> kafkaTemplate;

  @BeforeEach
  void setUp() {
    this.kafkaTemplate = mock(KafkaTemplate.class);
    dispatchOrderService = new DispatchOrderService(kafkaTemplate);
  }

  @Test
  void processSuccess() throws Exception {
    when(kafkaTemplate.send(anyString(), any(OrderDispatched.class)))
        .thenReturn(mock(CompletableFuture.class));

    when(kafkaTemplate.send(anyString(), any(DispatchPreparing.class)))
        .thenReturn(mock(CompletableFuture.class));

    var payload = "test-payload";
    dispatchOrderService.process(new OrderCreated(UUID.randomUUID(), payload));
    verify(kafkaTemplate).send(eq("order.dispatched"), any(OrderDispatched.class));
    verify(kafkaTemplate).send(eq("dispatch.tracking"), any(DispatchPreparing.class));
  }

  @Test
  void processFailedOnOrderDispatched() {
    doThrow(new RuntimeException("Producer failure"))
        .when(kafkaTemplate)
        .send(eq("order.dispatched"), any(OrderDispatched.class));

    var resultException =
        assertThrows(
            RuntimeException.class,
            () -> dispatchOrderService.process(new OrderCreated(UUID.randomUUID(), "test")));

    verify(kafkaTemplate).send(eq("order.dispatched"), any(OrderDispatched.class));
    assertThat(resultException.getMessage()).isEqualTo("Producer failure");
  }

  @Test
  void processFailedOnDispatchPreparing() {
    when(kafkaTemplate.send(anyString(), any(OrderDispatched.class)))
        .thenReturn(mock(CompletableFuture.class));

    doThrow(new RuntimeException("Producer failure"))
        .when(kafkaTemplate)
        .send(eq("dispatch.tracking"), any(DispatchPreparing.class));

    var resultException =
        assertThrows(
            RuntimeException.class,
            () -> dispatchOrderService.process(new OrderCreated(UUID.randomUUID(), "test")));

    verify(kafkaTemplate).send(eq("order.dispatched"), any(OrderDispatched.class));
    verify(kafkaTemplate).send(eq("dispatch.tracking"), any(DispatchPreparing.class));
    assertThat(resultException.getMessage()).isEqualTo("Producer failure");
  }
}
