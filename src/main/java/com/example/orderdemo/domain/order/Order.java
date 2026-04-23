package com.example.orderdemo.domain.order;

import com.example.orderdemo.common.exception.order.InvalidOrderException;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private long totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

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
            throw new InvalidOrderException("주문번호는 비어 있을 수 없습니다.");
        }
    }

    private void validateItems(List<OrderItem> items) {
        if (items.isEmpty()) {
            throw new InvalidOrderException("주문 항목은 비어 있을 수 없습니다.");
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
