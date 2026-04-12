package com.example.orderdemo.api.order;

import com.example.orderdemo.api.order.request.CreateOrderRequest;
import com.example.orderdemo.api.order.response.OrderCreateResponse;
import com.example.orderdemo.api.order.response.OrderDetailResponse;
import com.example.orderdemo.application.order.OrderCreateService;
import com.example.orderdemo.application.order.OrderQueryService;
import com.example.orderdemo.application.order.result.OrderCreateResult;
import com.example.orderdemo.application.order.result.OrderDetailResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderCreateService orderCreateService;
    private final OrderQueryService orderQueryService;

    public OrderController(OrderCreateService orderCreateService, OrderQueryService orderQueryService) {
        this.orderCreateService = orderCreateService;
        this.orderQueryService = orderQueryService;
    }

    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        OrderCreateResult result = orderCreateService.create(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderCreateResponse.from(result));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(@PathVariable Long orderId) {
        OrderDetailResult result = orderQueryService.getOrder(orderId);
        return ResponseEntity.ok(OrderDetailResponse.from(result));
    }
}
