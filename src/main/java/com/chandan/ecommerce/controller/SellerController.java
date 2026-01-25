package com.chandan.ecommerce.controller;

import com.chandan.ecommerce.modal.VerificationCode;
import com.chandan.ecommerce.repository.VerificationCodeRepository;
import com.chandan.ecommerce.request.LoginRequest;
import com.chandan.ecommerce.response.ApiResponse;
import com.chandan.ecommerce.response.AuthResponse;
import com.chandan.ecommerce.service.AuthService;
import com.chandan.ecommerce.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
        private final SellerService sellerService;
        private final VerificationCodeRepository verificationCodeRepository;
        private final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> loginSeller(
                @RequestBody LoginRequest req
                ) throws Exception {
            String otp=req.getOtp();
            String email=req.getEmail();
            req.setEmail("seller_"+email);
            AuthResponse authResponse=authService.signing(req);

            return ResponseEntity.ok(authResponse);
        }
}
