package com.example.orderdemo.application.order.result;

import com.example.orderdemo.domain.order.Order;
import com.example.orderdemo.domain.order.OrderStatus;

import java.util.List;

public record OrderDetailResult (
    Long orderId,
    String orderNumber,
    OrderStatus status,
    long totalAmount,
    List<OrderItemDetailResult> orderItems
) {
    public static OrderDetailResult from(Order order) {
        return new OrderDetailResult(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream().map(OrderItemDetailResult::from).toList()
        );
    }
}
