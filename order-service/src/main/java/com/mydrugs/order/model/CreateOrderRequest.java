package com.mydrugs.order.model;

import com.mydrugs.order.controller.OrderItemRequest;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
    @NotEmpty(message = "Total amount must be at least 1") long storeId,
    @NotEmpty(message = "Order must contain at least one item") List<OrderItemRequest> items
) {
}
