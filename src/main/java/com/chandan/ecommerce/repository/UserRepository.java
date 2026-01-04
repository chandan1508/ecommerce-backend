package com.chandan.ecommerce.repository;

import com.chandan.ecommerce.modal.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
