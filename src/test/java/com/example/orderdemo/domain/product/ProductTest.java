package com.example.orderdemo.domain.product;

import com.example.orderdemo.common.exception.product.OutOfStockException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void decreaseQuantity_재고차감_정상() {
        // given
        String name = "상품";
        long price = 10_000L;
        int quantity = 20;

        Product product = Product.create(name, price, quantity);

        // when
        product.decreaseQuantity(3);

        // then
        assertEquals(17, product.getQuantity());
    }

    @Test
    void decreaseQuantity_재고차감_실패() {
        // given
        String name = "상품";
        long price = 10_000L;
        int quantity = 20;

        Product product = Product.create(name, price, quantity);
        Long productId = product.getId();

        // when, then
        Throwable e = assertThrows(OutOfStockException.class, () -> product.decreaseQuantity(21));

        // then
        assertEquals("재고가 부족합니다. productId = " + productId, e.getMessage());
    }
}
