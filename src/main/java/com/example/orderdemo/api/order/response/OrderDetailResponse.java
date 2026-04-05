package com.example.orderdemo.api.order.response;

import com.example.orderdemo.application.order.result.OrderDetailResult;

import java.util.List;

public record OrderDetailResponse(
        Long orderId,
        String orderNumber,
        String status,
        long totalAmount,
        List<OrderItemResponse> orderItems
) {
    public static OrderDetailResponse from(OrderDetailResult result) {
        return new OrderDetailResponse(
                result.orderId(),
                result.orderNumber(),
                result.status().name(),
                result.totalAmount(),
                result.orderItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
