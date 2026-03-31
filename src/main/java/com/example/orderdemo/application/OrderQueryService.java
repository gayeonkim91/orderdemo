package com.example.orderdemo.application;

import com.example.orderdemo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderQueryService {
    private final OrderRepository orderRepository;
    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


}
