package com.example.orderdemo.application.order;

import com.example.orderdemo.application.order.result.OrderDetailResult;
import com.example.orderdemo.common.exception.order.OrderNotFoundException;
import com.example.orderdemo.domain.order.Order;
import com.example.orderdemo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional( readOnly = true)
public class OrderQueryService {
    private final OrderRepository orderRepository;
    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDetailResult getOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> new OrderNotFoundException(orderNumber));
        return OrderDetailResult.from(order);
    }
}
