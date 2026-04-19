package com.example.orderdemo.application.order.command;

public record CreateOrderLineCommand(
    Long productId,
    int quantity
) {
}
