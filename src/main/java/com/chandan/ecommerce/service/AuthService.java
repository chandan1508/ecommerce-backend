package com.chandan.ecommerce.service;

import com.chandan.ecommerce.response.SignupRequest;

public interface AuthService {
    String createUser(SignupRequest req);
}
