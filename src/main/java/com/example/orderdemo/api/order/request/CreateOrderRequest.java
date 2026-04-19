package com.example.orderdemo.api.order.request;

import com.example.orderdemo.application.order.command.CreateOrderCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "주문 항목은 비어있을 수 없습니다.")
        @Valid List<CreateOrderItemRequest> items
) {
    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(
                items.stream().map(CreateOrderItemRequest::toCommand).toList());
    }
}
