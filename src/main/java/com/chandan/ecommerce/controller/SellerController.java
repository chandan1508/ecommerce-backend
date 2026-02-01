package com.chandan.ecommerce.controller;

import com.chandan.ecommerce.domain.AccountStatus;
import com.chandan.ecommerce.modal.Seller;
import com.chandan.ecommerce.modal.VerificationCode;
import com.chandan.ecommerce.repository.VerificationCodeRepository;
import com.chandan.ecommerce.request.LoginRequest;
import com.chandan.ecommerce.response.AuthResponse;
import com.chandan.ecommerce.service.AuthService;
import com.chandan.ecommerce.service.EmailService;
import com.chandan.ecommerce.service.SellerService;
import com.chandan.ecommerce.utils.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
        private final SellerService sellerService;
        private final VerificationCodeRepository verificationCodeRepository;
        private final AuthService authService;
        private final EmailService emailService;

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

        @PatchMapping("/verify/{otp}")
        public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws Exception {
            // Use findByOtp which returns Optional
            Optional<VerificationCode> verificationCodeOpt = verificationCodeRepository.findByOtp(otp);

            if(!verificationCodeOpt.isPresent()) {
                throw new Exception("Invalid OTP");
            }

            VerificationCode verificationCode = verificationCodeOpt.get();

            Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);

            // Delete used OTP
            verificationCodeRepository.delete(verificationCode);

            return new ResponseEntity<>(seller, HttpStatus.OK);
        }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws Exception, MessagingException{
            Seller savedSeller = sellerService.createSeller(seller);

            String otp = OtpUtil.generateOtp();


        VerificationCode verificationCode=new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(seller.getEmail());
        verificationCodeRepository.save(verificationCode);

        String subject = "Zosh Bazaar Email Verification Code";
        String text = "Welcome to Zosh Bazaar, verify your account using this link";
        String frontend_url = "http://localhost:3000/verify-seller/";
        emailService.sendVerificationOtpEmail(seller.getEmail(), verificationCode.getOtp(), subject, text + frontend_url);
        return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws Exception {
            Seller seller=sellerService.getSellerById(id);
            return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(
            @RequestHeader("Authorization") String jwt
    ) throws Exception{
            Seller seller=sellerService.getSellerProfile(jwt);
            return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Seller>> getAllSellers(
            @RequestParam(required = false)AccountStatus status
            ){
            List<Seller> sellers=sellerService.getAllSellers(status);
            return ResponseEntity.ok(sellers);
    }

    @PatchMapping()
    public ResponseEntity<Seller> updateSeller(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Seller seller
    ) throws Exception{
            Seller profile=sellerService.getSellerProfile(jwt);
            Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
            return ResponseEntity.ok(updatedSeller);
    }

    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) throws Exception{
            sellerService.deleteSeller(id);
            return ResponseEntity.noContent().build();
    }



}
