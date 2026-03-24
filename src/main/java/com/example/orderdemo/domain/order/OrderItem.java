package com.example.orderdemo.domain.order;

public class OrderItem {
    private Long id;
    private Order order;
    private Long productId;
    private String productName;
    private long orderUnitPrice;
    private int quantity;

    public static OrderItem of(Long productId, String productName, long orderPrice, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.productId = productId;
        orderItem.productName = productName;
        orderItem.orderUnitPrice = orderPrice;
        orderItem.quantity = quantity;
        return orderItem;
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
}
