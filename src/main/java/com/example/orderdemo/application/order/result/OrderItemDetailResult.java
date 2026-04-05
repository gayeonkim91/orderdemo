package com.example.orderdemo.application.order.result;

import com.example.orderdemo.domain.order.OrderItem;

public record OrderItemDetailResult(
    Long productId,
    String productName,
    long orderUnitPrice,
    int quantity,
    long lineAmount
) {
    public static OrderItemDetailResult from(OrderItem orderItem) {
        return new OrderItemDetailResult(
                orderItem.getProductId(),
                orderItem.getProductName(),
                orderItem.lineAmount(),
                orderItem.getQuantity(),
                orderItem.lineAmount()
        );
    }
}
