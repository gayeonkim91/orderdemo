package com.example.orderdemo.domain.product;

import com.example.orderdemo.common.exception.product.OutOfStockException;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private long price;
    private int quantity;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Product create(String name, long price, int quantity) {
        return new Product(name, price, quantity);
    }

    public void decreaseQuantity(int decreaseQuantity) {
        if (decreaseQuantity > quantity) {
            throw new OutOfStockException(this.id);
        }
        this.quantity -= decreaseQuantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    protected Product() {}

    private Product(String name, long price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
