package com.chandan.ecommerce.repository;

import com.chandan.ecommerce.domain.AccountStatus;
import com.chandan.ecommerce.modal.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByEmail(String email);
    List<Seller> findByAccountStatus(AccountStatus status);
}
