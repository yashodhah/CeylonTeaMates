package com.teamates.model;

import java.util.List;

public record OrderCreationRequest(List<Product> products) {
}
