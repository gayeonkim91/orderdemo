package com.example.orderdemo.api.order.response;

import com.example.orderdemo.application.order.result.OrderCreateResult;

public record OrderCreateResponse(
        Long orderId,
        String orderNumber,
        String status,
        long totalAmount
) {
    public static OrderCreateResponse from(OrderCreateResult result) {
        return new OrderCreateResponse(
                result.orderId(),
                result.orderNumber(),
                result.status().name(),
                result.totalAmount()
        );
    }
}
