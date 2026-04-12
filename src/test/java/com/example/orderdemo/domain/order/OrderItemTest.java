package com.example.orderdemo.domain.order;

import com.example.orderdemo.common.exception.order.InvalidOrderException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void of_정상생성() {
        // given
        Long productId = 1L;
        String productName = "상품";
        long orderUnitPrice = 20_000L;
        int quantity = 2;

        // when
        OrderItem orderItem = OrderItem.of(productId, productName, orderUnitPrice, quantity);

        // then
        assertEquals(productId, orderItem.getProductId());
        assertEquals(productName, orderItem.getProductName());
        assertEquals(orderUnitPrice, orderItem.getOrderUnitPrice());
        assertEquals(quantity, orderItem.getQuantity());
    }

    @Test
    void of_주문수량오류() {
        // given
        Long productId = 1L;
        String productName = "상품";
        long orderUnitPrice = 20_000L;
        int quantity = -1;

        // when, then
        Throwable e = assertThrows(InvalidOrderException.class, () -> OrderItem.of(productId, productName, orderUnitPrice, quantity));

        // then (extra)
        assertEquals("주문 수량은 1 이상이어야 합니다.", e.getMessage());
    }

    @Test
    void lineAmount() {
        // given
        long orderUnitPrice = 10_000;
        int quantity = 3;
        OrderItem orderItem = OrderItem.of(1L, "상품", orderUnitPrice, quantity);

        // when
        long lineAmount = orderItem.lineAmount();

        // then
        assertEquals(orderUnitPrice * quantity, lineAmount);
    }
}
