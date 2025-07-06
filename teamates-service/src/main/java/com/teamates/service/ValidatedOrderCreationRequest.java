package com.teamates.service;

import com.teamates.model.CreateOrderRequest;
import com.teamates.model.Product;
import com.teamates.model.Store;

import java.util.List;

public record ValidatedOrderCreationRequest(
        Store store,
        List<Product> products,
        CreateOrderRequest originalRequest
) {}
