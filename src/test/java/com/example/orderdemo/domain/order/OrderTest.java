package com.example.orderdemo.domain.order;

import com.example.orderdemo.common.exception.order.InvalidOrderException;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void createOrder_정상() {
        // given
        OrderItem orderItem1 = OrderItem.of(1L, "상품1", 10_000L, 2);
        OrderItem orderItem2 = OrderItem.of(2L, "상품2", 20_000L, 3);
        String orderNumber = "111";
        List<OrderItem> items = List.of(orderItem1, orderItem2);

        // when
        Order order = Order.create(orderNumber, items);

        // then
        assertNotNull(order);
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(items, order.getItems());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(orderItem1.lineAmount() + orderItem2.lineAmount(), order.getTotalAmount());
    }

    @Test
    void createOrder_주문번호_null() {
        // given
        OrderItem orderItem1 = OrderItem.of(1L, "상품1", 10_000L, 2);
        OrderItem orderItem2 = OrderItem.of(2L, "상품2", 20_000L, 3);
        List<OrderItem> items = List.of(orderItem1, orderItem2);

        // when, then
        Throwable e = assertThrows(InvalidOrderException.class, () -> Order.create(null, items));

        // then (extra)
        assertEquals("주문번호는 비어 있을 수 없습니다.", e.getMessage());
    }

    @Test
    void createOrder_주문번호_blank() {
        // given
        OrderItem orderItem1 = OrderItem.of(1L, "상품1", 10_000L, 2);
        OrderItem orderItem2 = OrderItem.of(2L, "상품2", 20_000L, 3);
        List<OrderItem> items = List.of(orderItem1, orderItem2);

        // when, then
        Throwable e = assertThrows(InvalidOrderException.class, () -> Order.create(Strings.EMPTY, items));

        // then (extra)
        assertEquals("주문번호는 비어 있을 수 없습니다.", e.getMessage());
    }

    @Test
    void createOrder_아이템없음() {
        // given
        String orderNumber = "111";
        List<OrderItem> items = List.of();

        // when, then
        Throwable e = assertThrows(InvalidOrderException.class, () -> Order.create(orderNumber, items));

        // then (extra)
        assertEquals("주문 항목은 비어 있을 수 없습니다.", e.getMessage());
    }
}
