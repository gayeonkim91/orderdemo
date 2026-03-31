package com.example.orderdemo.application.order.result;

import com.example.orderdemo.domain.order.Order;

public record OrderCreateResult (

) {
    public static OrderCreateResult from(Order order) {
        return new OrderCreateResult();
    }
}
