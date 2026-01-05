package com.chandan.ecommerce.service;

import com.chandan.ecommerce.response.SignupRequest;

public interface AuthService {
    void sentLoginOtp(String email) throws Exception;
    String createUser(SignupRequest req) throws Exception;
}
