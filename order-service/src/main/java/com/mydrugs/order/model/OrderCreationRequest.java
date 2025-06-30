package com.mydrugs.order.model;

import java.util.List;

public record OrderCreationRequest(List<Product> products) {
}
