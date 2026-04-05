package com.example.orderdemo.api.order.response;

import com.example.orderdemo.application.order.result.OrderItemDetailResult;

public record OrderItemResponse(
        Long productId,
        String productName,
        long orderUnitPrice,
        int quantity,
        long lineAmount
) {
    public static OrderItemResponse from(OrderItemDetailResult result) {
        return new OrderItemResponse(
                result.productId(),
                result.productName(),
                result.orderUnitPrice(),
                result.quantity(),
                result.lineAmount()
        );
    }
}
