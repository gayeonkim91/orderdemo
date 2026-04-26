package com.example.orderdemo.application.order;

import com.example.orderdemo.application.order.command.CreateOrderCommand;
import com.example.orderdemo.application.order.command.CreateOrderLineCommand;
import com.example.orderdemo.common.exception.product.OutOfStockException;
import com.example.orderdemo.domain.product.Product;
import com.example.orderdemo.repository.OrderRepository;
import com.example.orderdemo.repository.ProductRepository;
import com.example.orderdemo.support.MySqlTestContainerSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderCreateServiceConcurrencyTest extends MySqlTestContainerSupport {
    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 동시에_주문해도_재고보다_많이_성공할_수_없다() throws Exception {
        // given
        Product product = productRepository.save(Product.create("상품1", 1000, 10));
        Long productId = product.getId();

        int requestCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch readyLatch = new CountDownLatch(requestCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger outOfStockCount = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < requestCount; i++) {
            futures.add(executorService.submit(() -> {
                readyLatch.countDown();
                startLatch.await();

                try {
                    CreateOrderCommand command = new CreateOrderCommand(
                            List.of(new CreateOrderLineCommand(productId, 1))
                    );

                    orderCreateService.create(command);
                    successCount.incrementAndGet();
                } catch (OutOfStockException e) {
                    outOfStockCount.incrementAndGet();
                }

                return null;
            }));
        }

        readyLatch.await();
        startLatch.countDown();

        for (Future<?> future : futures) {
            future.get();
        }

        executorService.shutdown();

        // then
        Product savedProduct = productRepository.findById(productId).orElseThrow();

        assertEquals(10, successCount.get());
        assertEquals(10, outOfStockCount.get());
        assertEquals(0, savedProduct.getQuantity());
        assertEquals(10, orderRepository.count());
    }

}
