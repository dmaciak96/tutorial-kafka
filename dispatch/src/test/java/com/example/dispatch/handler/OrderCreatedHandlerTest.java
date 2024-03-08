package com.example.dispatch.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.dispatch.message.OrderCreated;
import com.example.dispatch.service.DispatchOrderService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderCreatedHandlerTest {

  private OrderCreatedHandler orderCreatedHandler;
  private DispatchOrderService dispatchOrderServiceMock;

  @BeforeEach
  void setUp() {
    this.dispatchOrderServiceMock = mock(DispatchOrderService.class);
    this.orderCreatedHandler = new OrderCreatedHandler(dispatchOrderServiceMock);
  }

  @Test
  void listen() throws Exception {
    var payload = new OrderCreated(UUID.randomUUID(), "test-payload");
    orderCreatedHandler.listen(payload);
    verify(dispatchOrderServiceMock).process(payload);
  }
}
