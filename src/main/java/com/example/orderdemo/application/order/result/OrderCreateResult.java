package com.example.orderdemo.application.order.result;

import com.example.orderdemo.domain.order.Order;
import com.example.orderdemo.domain.order.OrderStatus;

public record OrderCreateResult (
    Long orderId,
    String orderNumber,
    OrderStatus status,
    long totalAmount
) {
    public static OrderCreateResult from(Order order) {
        return new OrderCreateResult(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getTotalAmount()
        );
    }
}
