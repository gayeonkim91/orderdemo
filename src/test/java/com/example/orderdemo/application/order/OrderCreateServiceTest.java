package com.example.orderdemo.application.order;

import com.example.orderdemo.application.order.command.CreateOrderCommand;
import com.example.orderdemo.application.order.command.CreateOrderLineCommand;
import com.example.orderdemo.application.order.result.OrderCreateResult;
import com.example.orderdemo.common.exception.order.InvalidOrderException;
import com.example.orderdemo.common.exception.product.OutOfStockException;
import com.example.orderdemo.common.exception.product.ProductNotFoundException;
import com.example.orderdemo.domain.order.Order;
import com.example.orderdemo.domain.product.Product;
import com.example.orderdemo.repository.OrderRepository;
import com.example.orderdemo.repository.ProductRepository;
import com.example.orderdemo.support.MySqlTestContainerSupport;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderCreateServiceTest extends MySqlTestContainerSupport {
    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void create_정상() {
        // given
        int initialQuantity = 100;
        Product product1 = productRepository.save(Product.create("상품1", 1000, initialQuantity));
        Product product2 = productRepository.save(Product.create("상품2", 2300, initialQuantity));
        CreateOrderLineCommand orderLine1 = new CreateOrderLineCommand(product1.getId(), 1);
        CreateOrderLineCommand orderLine2 = new CreateOrderLineCommand(product2.getId(), 2);
        List<CreateOrderLineCommand> createOrderLineCommands = List.of(orderLine1, orderLine2);
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(createOrderLineCommands);

        // when
        OrderCreateResult result = orderCreateService.create(createOrderCommand);

        // then
        assertNotNull(result);
        Order saved = orderRepository.findById(result.orderId()).orElse(null);
        assertNotNull(saved);
        assertEquals(saved.getOrderNumber(), result.orderNumber());
        assertEquals(saved.getStatus(), result.status());
        assertEquals(product1.getPrice() * orderLine1.quantity() + product2.getPrice() * orderLine2.quantity(), result.totalAmount());

        assertEquals(initialQuantity - orderLine1.quantity(), product1.getQuantity());
        assertEquals(initialQuantity - orderLine2.quantity(), product2.getQuantity());
    }

    @Test
    void create_상품아이디중복() {
        int initialQuantity = 100;
        Product product1 = productRepository.save(Product.create("상품1", 1000, initialQuantity));
        Product product2 = productRepository.save(Product.create("상품2", 2300, initialQuantity));
        CreateOrderLineCommand orderLine1 = new CreateOrderLineCommand(product1.getId(), 1);
        CreateOrderLineCommand orderLine2 = new CreateOrderLineCommand(product2.getId(), 2);
        CreateOrderLineCommand orderLine3 = new CreateOrderLineCommand(product2.getId(), 3);
        List<CreateOrderLineCommand> createOrderLineCommands = List.of(orderLine1, orderLine2, orderLine3);
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(createOrderLineCommands);

        // when, then
        Throwable e = assertThrows(InvalidOrderException.class,() -> orderCreateService.create(createOrderCommand));

        // then (extra)
        assertEquals("중복된 상품 ID는 주문할 수 없습니다.", e.getMessage());
    }

    @Test
    void create_존재하지않는상품아이디() {
        int initialQuantity = 100;
        Product product1 = productRepository.save(Product.create("상품1", 1000, initialQuantity));
        Product product2 = productRepository.save(Product.create("상품2", 2300, initialQuantity));
        CreateOrderLineCommand orderLine1 = new CreateOrderLineCommand(product1.getId(), 1);
        CreateOrderLineCommand orderLine2 = new CreateOrderLineCommand(product2.getId(), 2);
        Long productId = 3L;
        CreateOrderLineCommand orderLine3 = new CreateOrderLineCommand(productId, 3);
        List<CreateOrderLineCommand> createOrderLineCommands = List.of(orderLine1, orderLine2, orderLine3);
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(createOrderLineCommands);

        // when, then
        Throwable e = assertThrows(ProductNotFoundException.class,() -> orderCreateService.create(createOrderCommand));

        // then
        assertEquals("상품을 찾을 수 없습니다. productIds = " + List.of(productId), e.getMessage());
    }

    @Test
    void create_재고부족() {
        int initialQuantity = 1;
        Product product = productRepository.save(Product.create("상품1", 1000, initialQuantity));
        CreateOrderLineCommand orderLine = new CreateOrderLineCommand(product.getId(), 3);
        List<CreateOrderLineCommand> createOrderLineCommands = List.of(orderLine);
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(createOrderLineCommands);

        // when, then
        Throwable e = assertThrows(OutOfStockException.class,() -> orderCreateService.create(createOrderCommand));

        // then
        assertEquals("재고가 부족합니다. productId = " + product.getId(), e.getMessage());
    }
}
