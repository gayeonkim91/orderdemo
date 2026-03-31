package com.example.orderdemo.application;

import com.example.orderdemo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderCreateService {
    private final OrderRepository orderRepository;
    public OrderCreateService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

}
