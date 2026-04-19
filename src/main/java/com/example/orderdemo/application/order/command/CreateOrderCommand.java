package com.example.orderdemo.application.order.command;

import java.util.List;

public record CreateOrderCommand(
        List<CreateOrderLineCommand> items
) {
}
