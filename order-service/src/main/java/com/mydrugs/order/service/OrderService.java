package com.mydrugs.order.service;

import com.mydrugs.order.model.CreateOrderRequest;
import com.mydrugs.order.messaging.EventPublisher;
import com.mydrugs.order.model.Order;
import com.mydrugs.order.model.OrderItem;
import com.mydrugs.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private  OrderRepository orderRepository;
    @Autowired
    private OrderValidationService orderValidationService;
    @Autowired
    private  EventPublisher orderEventPublisher;

    @Transactional
    public Order createOrder(CreateOrderRequest createOrderRequest) {

        orderValidationService.validate(createOrderRequest);
        // Fetch products & validate stock
        List<OrderItem> orderItems = createOrderRequest.items().stream()
                .map(item -> {

                    return OrderItem.builder()
                            .product(product)
                            .quantity(item.quantity())
                            .build();
                })
                .collect(Collectors.toList());

        // we need third party payment service to process payment

        // Create order entity
        Order order = Order.builder()
                .orderNumber("ORD-" + System.currentTimeMillis()) // Meaningful order number
                .status(Order.OrderStatus.PENDING)
                .createdAt(Instant.now())
                .items(orderItems)
                .build();

        // Set the parent order to each order item, circular reference here
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        // Save order & publish event
        Order savedOrder = orderRepository.save(order);

        // TODO: Send only required, Don't send the whole order
        orderEventPublisher.publishEvent(savedOrder);

        return savedOrder;
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
