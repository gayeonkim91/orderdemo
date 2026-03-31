package com.example.orderdemo.application.order.result;

import com.example.orderdemo.domain.order.Order;

public record OrderDetailResult (

) {
    public static OrderDetailResult from(Order order) {
        return new OrderDetailResult();
    }
}
