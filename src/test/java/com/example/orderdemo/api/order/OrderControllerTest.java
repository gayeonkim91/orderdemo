package com.example.orderdemo.api.order;

import com.example.orderdemo.api.order.request.CreateOrderItemRequest;
import com.example.orderdemo.api.order.request.CreateOrderRequest;
import com.example.orderdemo.application.order.OrderCreateService;
import com.example.orderdemo.application.order.OrderQueryService;
import com.example.orderdemo.application.order.command.CreateOrderCommand;
import com.example.orderdemo.application.order.result.OrderCreateResult;
import com.example.orderdemo.application.order.result.OrderDetailResult;
import com.example.orderdemo.application.order.result.OrderItemDetailResult;
import com.example.orderdemo.common.exception.order.OrderNotFoundException;
import com.example.orderdemo.common.exception.product.OutOfStockException;
import com.example.orderdemo.common.exception.product.ProductNotFoundException;
import com.example.orderdemo.domain.order.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderCreateService orderCreateService;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @Test
    void createOrder_성공() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(1L, 1),
                        new CreateOrderItemRequest(2L, 3)
                )
        );
        OrderCreateResult result = new OrderCreateResult(10L, "ORD-12345", OrderStatus.CREATED, 5000L);
        given(orderCreateService.create(any(CreateOrderCommand.class))).willReturn(result);

        //when, then
        mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(10L))
                .andExpect(jsonPath("$.orderNumber").value("ORD-12345"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(5000L));
    }

    @Test
    void createOrder_주문항목빔() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(List.of());
        OrderCreateResult result = new OrderCreateResult(10L, "ORD-12345", OrderStatus.CREATED, 5000L);
        given(orderCreateService.create(any(CreateOrderCommand.class))).willReturn(result);

        //when, then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("주문 항목은 비어있을 수 없습니다."));
    }

    @Test
    void createOrder_orderItemId_없음() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(null, 1)
                )
        );
        OrderCreateResult result = new OrderCreateResult(10L, "ORD-12345", OrderStatus.CREATED, 5000L);
        given(orderCreateService.create(any(CreateOrderCommand.class))).willReturn(result);

        //when, then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("상품 ID는 필수입니다."));
    }

    @Test
    void createOrder_orderItem_수량없음() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(1L, null)
                )
        );
        OrderCreateResult result = new OrderCreateResult(10L, "ORD-12345", OrderStatus.CREATED, 5000L);
        given(orderCreateService.create(any(CreateOrderCommand.class))).willReturn(result);

        //when, then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("수량은 필수입니다."));
    }

    @Test
    void createOrder_orderItem_수량1미만() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(1L, -2)
                )
        );
        OrderCreateResult result = new OrderCreateResult(10L, "ORD-12345", OrderStatus.CREATED, 5000L);
        given(orderCreateService.create(any(CreateOrderCommand.class))).willReturn(result);

        //when, then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.message").value("수량은 1 이상이어야 합니다."));
    }

    @Test
    void createOrder_orderItem_존재하지않는상품() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(1L, 1)
                )
        );
        given(orderCreateService.create(any(CreateOrderCommand.class))).willThrow(new ProductNotFoundException(List.of(1L)));

        //when, then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다. productIds = " + List.of(1L).toString()));
    }

    @Test
    void createOrder_orderItem_재고부족() throws Exception {
        // given
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                List.of(
                        new CreateOrderItemRequest(1L, 2)
                )
        );
        given(orderCreateService.create(any(CreateOrderCommand.class))).willThrow(new OutOfStockException(1L));

        //when, then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("OUT_OF_STOCK"))
                .andExpect(jsonPath("$.message").value("재고가 부족합니다. productId = " + 1L));
    }

    @Test
    void getOrder_성공() throws Exception {
        // given
        OrderDetailResult result = new OrderDetailResult(
                1L, "ORD-12345", OrderStatus.CREATED, 3000L,
                List.of(
                        new OrderItemDetailResult(11L, "상품1", 1000L, 1, 1000L),
                        new OrderItemDetailResult(12L, "상품2", 500L, 4, 2000L)
                )
        );
        given(orderQueryService.getOrder(anyString())).willReturn(result);

        // when, then
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.orderNumber").value("ORD-12345"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(3000L))
                .andExpect(jsonPath("$.orderItems[0].productId").value(11L))
                .andExpect(jsonPath("$.orderItems[0].productName").value("상품1"))
                .andExpect(jsonPath("$.orderItems[0].orderUnitPrice").value(1000L))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(1))
                .andExpect(jsonPath("$.orderItems[0].lineAmount").value(1000L))
                .andExpect(jsonPath("$.orderItems[1].productId").value(12L))
                .andExpect(jsonPath("$.orderItems[1].productName").value("상품2"))
                .andExpect(jsonPath("$.orderItems[1].orderUnitPrice").value(500L))
                .andExpect(jsonPath("$.orderItems[1].quantity").value(4))
                .andExpect(jsonPath("$.orderItems[1].lineAmount").value(2000L))
        ;
    }

    @Test
    void getOrder_주문없음() throws Exception {
        // given
        String orderNumber = "ORD-12345";
        given(orderQueryService.getOrder(anyString())).willThrow(new OrderNotFoundException(orderNumber));

        // when, then
        mockMvc.perform(get("/api/orders/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다. orderNumber = " + orderNumber))
        ;
    }
}
