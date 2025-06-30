package com.mydrugs.orderprocessing.repository;

import com.mydrugs.orderprocessing.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}

