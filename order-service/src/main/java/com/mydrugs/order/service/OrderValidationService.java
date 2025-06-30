package com.mydrugs.order.service;

import com.mydrugs.order.model.CreateOrderRequest;
import com.mydrugs.order.model.OrderCreationRequest;
import com.mydrugs.order.model.Product;
import com.mydrugs.order.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderValidationService {

    @Autowired
    ProductRepository productRepository;

    public OrderCreationRequest validateOrderCreationRequest(CreateOrderRequest request) {
        // Validate the order creation request
        if (request == null) {
            throw new IllegalArgumentException("Order creation request cannot be null");
        }

        OrderCreationRequest orderCreationRequest =
        request.items().forEach(item -> {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.productId()));
        });

        // ned to validate against the particular store as well


        return request;
    }



}
