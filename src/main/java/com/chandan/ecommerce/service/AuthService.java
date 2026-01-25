package com.chandan.ecommerce.service;

import com.chandan.ecommerce.domain.USER_ROLE;
import com.chandan.ecommerce.request.LoginRequest;
import com.chandan.ecommerce.response.AuthResponse;
import com.chandan.ecommerce.response.SignupRequest;

public interface AuthService {
    void sentLoginOtp(String email, USER_ROLE role) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req);
}
