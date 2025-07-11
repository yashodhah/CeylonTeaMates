package com.teamates.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;  // Reference to the order

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  // Reference to the product

    @Column(nullable = false)
    private int quantity;  // How many units of this product were ordered

    @Column(nullable = false)
    private BigDecimal price;
}
