package com.example.orderdemo.domain.order;

import com.example.orderdemo.common.InvalidOrderException;
import com.example.orderdemo.common.InvalidProductException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    private Long productId;
    private String productName;
    private long orderUnitPrice;
    private int quantity;

    private LocalDateTime createdAt;
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
        validateProductId(productId);
        validateProductName(productName);
        validateOrderUnitPrice(orderUnitPrice);
        validateQuantity(quantity);

        this.productId = productId;
        this.productName = productName;
        this.orderUnitPrice = orderUnitPrice;
        this.quantity = quantity;
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId < 0) {
            throw new InvalidProductException();
        }
    }

    private void validateProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new InvalidProductException();
        }
    }

    private void validateOrderUnitPrice(long orderUnitPrice) {
        if (orderUnitPrice < 0) {
            throw new InvalidOrderException();
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new InvalidOrderException();
        }
    }
}
