package com.example.orderdemo.domain.order;

import com.example.orderdemo.common.exception.order.InvalidOrderException;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private long orderUnitPrice;

    @Column(nullable = false)
    private int quantity;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static OrderItem of(Long productId, String productName, long orderUnitPrice, int quantity) {
        return new OrderItem(productId, productName, orderUnitPrice, quantity);
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public long getOrderUnitPrice() {
        return orderUnitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

    public long lineAmount() {
        return orderUnitPrice * quantity;
    }

    protected OrderItem() {}

    private OrderItem(Long productId, String productName, long orderUnitPrice, int quantity) {
        validateQuantity(quantity);

        this.productId = productId;
        this.productName = productName;
        this.orderUnitPrice = orderUnitPrice;
        this.quantity = quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidOrderException("주문 수량은 1 이상이어야 합니다.");
        }
    }
}
