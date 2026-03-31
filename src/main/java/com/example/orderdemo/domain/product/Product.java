package com.example.orderdemo.domain.product;

import com.example.orderdemo.common.InvalidProductException;
import com.example.orderdemo.common.OutOfStockException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private long price;
    private int quantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Product create(String name, long price, int quantity) {
        return new Product(name, price, quantity);
    }

    public void decreaseQuantity(int decreaseQuantity) {
        if (decreaseQuantity < 0) {
            throw new InvalidProductException();
        }
        if (decreaseQuantity > quantity) {
            throw new OutOfStockException();
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
        validateName(name);
        validatePrice(price);
        validateQuantity(quantity);

        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidProductException();
        }
        // todo 길이
    }

    private void validatePrice(long price) {
        if (price < 0) {
            throw new InvalidProductException();
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new InvalidProductException();
        }
    }

}
