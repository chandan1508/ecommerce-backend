package com.chandan.ecommerce.repository;

import com.chandan.ecommerce.modal.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByOtp(String otp);
    List<VerificationCode> findAllByEmail(String email);
}
