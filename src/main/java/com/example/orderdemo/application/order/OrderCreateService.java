package com.example.orderdemo.application.order;

import com.example.orderdemo.application.order.command.CreateOrderCommand;
import com.example.orderdemo.application.order.command.CreateOrderLineCommand;
import com.example.orderdemo.application.order.result.OrderCreateResult;
import com.example.orderdemo.common.exception.order.InvalidOrderException;
import com.example.orderdemo.common.exception.product.ProductNotFoundException;
import com.example.orderdemo.domain.order.Order;
import com.example.orderdemo.domain.order.OrderItem;
import com.example.orderdemo.domain.product.Product;
import com.example.orderdemo.repository.OrderRepository;
import com.example.orderdemo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderCreateService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderNumberGenerator orderNumberGenerator;
    public OrderCreateService(OrderRepository orderRepository, ProductRepository productRepository, OrderNumberGenerator orderNumberGenerator) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderNumberGenerator = orderNumberGenerator;
    }

    public OrderCreateResult create(CreateOrderCommand command) {
        List<Long> productIds = command.items().stream()
                .map(CreateOrderLineCommand::productId)
                .toList();

        validateNoDuplicateProductIds(productIds);
        Map<Long, Product> productMap = getProductMap(productIds);


        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderLineCommand item : command.items()) {
            Product product = productMap.get(item.productId());
            product.decreaseQuantity(item.quantity());
            OrderItem orderItem = OrderItem.of(item.productId(), product.getName(), product.getPrice(), item.quantity());
            orderItems.add(orderItem);
        }

        String orderNumber = orderNumberGenerator.generate();
        Order createdOrder = orderRepository.save(Order.create(orderNumber, orderItems));
        return OrderCreateResult.from(createdOrder);
    }

    private void validateNoDuplicateProductIds(List<Long> productIds) {
        if (productIds.size() != new HashSet<>(productIds).size()) {
            throw new InvalidOrderException("중복된 상품 ID는 주문할 수 없습니다.");
        }
    }

    private Map<Long, Product> getProductMap(List<Long> productIds) {
        List<Product> foundProducts = productRepository.findAllById(productIds);
        validateProductsFound(productIds, foundProducts);
        return foundProducts.stream().collect(Collectors.toMap(Product::getId, p -> p));
    }

    private void validateProductsFound(List<Long> productIds, List<Product> foundProducts) {
        if(productIds.size() != foundProducts.size()) {
            Set<Long> foundProductIds = new HashSet<>(productIds);
            throw new ProductNotFoundException(productIds.stream()
                    .filter(id -> !foundProductIds.contains(id)).toList());
        }
    }
}
