package com.teamates.service;

import com.teamates.model.CreateOrderRequest;
import com.teamates.model.Order;
import com.teamates.model.OrderItem;
import com.teamates.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class OrderService {

    @Autowired
    private  OrderRepository orderRepository;
    @Autowired
    private OrderValidationService orderValidationService;

    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest) {

        ValidatedOrderCreationRequest validated = orderValidationService.validateOrderCreationRequest(createOrderRequest);
        List<OrderItem> orderItems = IntStream.range(0, validated.products().size())
                .mapToObj(i -> OrderItem.builder()
                        .product(validated.products().get(i))
                        .quantity(createOrderRequest.items().get(i).quantity())
                        .price(validated.products().get(i).getPrice())
                        .build())
                .toList();

        // TODO: we need third party payment service to process payment.

        // Create order entity
        Order order = Order.builder()
                .orderNumber("ORD-" + System.currentTimeMillis()) // TODO: Meaningful order number
                .status(Order.OrderStatus.PENDING)
                .createdAt(Instant.now())
                .items(orderItems)
                .storeId(createOrderRequest.storeId())
                .customerId("demo-customer") // Placeholder for customer ID, should be replaced with actual customer ID
                .amount(orderItems.stream()
                        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)) // Calculate total amount
                .build();

        // Set the parent order to each order item, circular reference here
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        // Save order & publish event
        return orderRepository.save(order);
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
