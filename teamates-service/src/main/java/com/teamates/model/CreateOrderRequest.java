package com.teamates.model;

import com.teamates.controller.OrderItemRequest;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
    @NotEmpty(message = "Total amount must be at least 1") int storeId,
    @NotEmpty(message = "Order must contain at least one item") List<OrderItemRequest> items
) {
}
