package com.chandan.ecommerce.service.impl;

import com.chandan.ecommerce.config.JwtProvider;
import com.chandan.ecommerce.domain.USER_ROLE;
import com.chandan.ecommerce.modal.Cart;
import com.chandan.ecommerce.modal.Seller;
import com.chandan.ecommerce.modal.User;
import com.chandan.ecommerce.modal.VerificationCode;
import com.chandan.ecommerce.repository.CartRepository;
import com.chandan.ecommerce.repository.SellerRepository;
import com.chandan.ecommerce.repository.UserRepository;
import com.chandan.ecommerce.repository.VerificationCodeRepository;
import com.chandan.ecommerce.request.LoginRequest;
import com.chandan.ecommerce.response.AuthResponse;
import com.chandan.ecommerce.response.SignupRequest;
import com.chandan.ecommerce.service.AuthService;
import com.chandan.ecommerce.service.EmailService;
import com.chandan.ecommerce.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserService;
    private final SellerRepository sellerRepository;

    @Override
    public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
        String SIGNING_PREFIX="signing_";

        if(email.startsWith(SIGNING_PREFIX)){
            email=email.substring(SIGNING_PREFIX.length());

            if(role.equals(USER_ROLE.ROLE_SELLER)){
                Seller seller=sellerRepository.findByEmail(email);
                if(seller==null){
                    throw new Exception("seller not found");
                }
            }
            else {
                User user = userRepository.findByEmail(email);
                if(user==null){
                    throw new Exception("user not exist with provided email");
                }
            }
        }

        // Delete all previous OTPs for this email
        List<VerificationCode> existingCodes = verificationCodeRepository.findAllByEmail(email);
        if(!existingCodes.isEmpty()) {
            verificationCodeRepository.deleteAll(existingCodes);
        }

        // Generate and save new OTP
        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = "My bazaar login/signup otp";
        String text = "your login/signup otp is - " + otp;

        emailService.sendVerificationOtpEmail(email, otp, subject, text);
    }

    @Override
    public String createUser(SignupRequest req) throws Exception {
        // Use findAllByEmail and get the latest one
        List<VerificationCode> verificationCodes = verificationCodeRepository.findAllByEmail(req.getEmail());

        if(verificationCodes.isEmpty()){
            throw new Exception("Please request OTP first");
        }

        // Get the most recent verification code
        VerificationCode verificationCode = verificationCodes.get(verificationCodes.size() - 1);

        if(!verificationCode.getOtp().equals(req.getOtp())){
            throw new Exception("wrong otp..");
        }

        User user = userRepository.findByEmail(req.getEmail());

        if(user == null){
            User createdUser = new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("8596455241");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));

            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        // Delete used OTP
        verificationCodeRepository.delete(verificationCode);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);
    }

    @Override
    public AuthResponse signing(LoginRequest req) throws Exception {
        String username = req.getEmail();
        String otp = req.getOtp();

        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login success");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;
    }

    private Authentication authenticate(String username, String otp) throws Exception {
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        String SELLER_PREFIX = "seller_";
        if(username.startsWith(SELLER_PREFIX)){
            username = username.substring(SELLER_PREFIX.length());
        }

        if(userDetails == null){
            throw new BadCredentialsException("invalid username or password");
        }

        // Use findAllByEmail and get the latest one
        List<VerificationCode> verificationCodes = verificationCodeRepository.findAllByEmail(username);

        if(verificationCodes.isEmpty()){
            throw new Exception("Please request OTP first");
        }

        // Get the most recent verification code
        VerificationCode verificationCode = verificationCodes.get(verificationCodes.size() - 1);

        if(!verificationCode.getOtp().equals(otp)){
            throw new Exception("wrong otp");
        }

        // Delete used OTP
        verificationCodeRepository.delete(verificationCode);

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }
}