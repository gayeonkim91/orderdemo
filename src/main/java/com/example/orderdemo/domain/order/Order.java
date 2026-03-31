package com.example.orderdemo.domain.order;

import com.example.orderdemo.common.InvalidOrderException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private long totalAmount;
    private List<OrderItem> items = new ArrayList<>();

    public static Order create(String orderNumber, List<OrderItem> items) {
        return new Order(orderNumber, items);
    }

    public Long getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    protected Order() {}

    private Order(String orderNumber, List<OrderItem> items) {
        validateOrderNumber(orderNumber);
        validateItems(items);

        this.orderNumber = orderNumber;
        this.status = OrderStatus.CREATED;

        items.forEach(this::addItem);
        this.totalAmount = calculateTotalAmount();
    }

    private void validateOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.isBlank()) {
            throw new InvalidOrderException();
        }
        // todo 중복검사?
    }

    private void validateItems(List<OrderItem> items) {
        if (items.isEmpty()) {
            throw new InvalidOrderException();
        }
    }

    private void addItem(OrderItem item) {
        this.items.add(item);
        item.assignOrder(this);
    }

    private long calculateTotalAmount() {
        return items.stream().mapToLong(OrderItem::lineAmount).sum();
    }
}
