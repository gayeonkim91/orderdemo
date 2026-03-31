package com.example.orderdemo.application.order;

import com.example.orderdemo.application.order.command.CreateOrderCommand;
import com.example.orderdemo.application.order.command.CreateOrderLineCommand;
import com.example.orderdemo.application.order.result.OrderCreateResult;
import com.example.orderdemo.domain.order.Order;
import com.example.orderdemo.domain.order.OrderItem;
import com.example.orderdemo.domain.product.Product;
import com.example.orderdemo.repository.OrderRepository;
import com.example.orderdemo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public OrderCreateResult createOrder(CreateOrderCommand command) {
        String orderNumber = orderNumberGenerator.generate();
        List<OrderItem> orderItemList = convertOrderItems(command.items());
        Order order = Order.create(orderNumber, orderItemList);
        return OrderCreateResult.from(orderRepository.save(order));
    }

    private List<OrderItem> convertOrderItems(List<CreateOrderLineCommand> items) {
        List<OrderItem> result = new ArrayList<>();
        List<Long> productIds = items.stream().map(CreateOrderLineCommand::productId).toList();
        List<Product> products = productRepository.findAllById(productIds);
        for (int i = 0; i < items.size(); i++) {
            CreateOrderLineCommand item = items.get(i);
            Product product = products.get(i);
            result.add(convertOrderItem(product, item.quantity()));
        }
        return result;
    }

    private OrderItem convertOrderItem(Product product, int quantity) {
        return OrderItem.of(product.getId(), product.getName(), product.getPrice(), quantity);
    }
}
