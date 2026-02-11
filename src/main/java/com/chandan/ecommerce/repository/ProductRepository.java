package com.chandan.ecommerce.repository;

import com.chandan.ecommerce.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
