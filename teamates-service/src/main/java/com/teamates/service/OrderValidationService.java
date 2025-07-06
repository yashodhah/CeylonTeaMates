package com.teamates.service;

import com.teamates.model.CreateOrderRequest;
import com.teamates.model.OrderCreationRequest;
import com.teamates.model.Product;
import com.teamates.model.Store;
import com.teamates.repository.ProductRepository;
import com.teamates.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderValidationService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    public ValidatedOrderCreationRequest validateOrderCreationRequest(CreateOrderRequest request) {
        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + request.storeId()));

        List<Product> products = request.items().stream()
                .map(item -> productRepository.findById(item.productId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.productId())))
                .toList();

        return new ValidatedOrderCreationRequest(store, products, request);
    }
}
