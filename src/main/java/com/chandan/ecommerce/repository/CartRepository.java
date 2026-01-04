package com.chandan.ecommerce.repository;

import com.chandan.ecommerce.modal.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
