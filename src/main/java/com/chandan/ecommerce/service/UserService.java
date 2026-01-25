package com.chandan.ecommerce.service;

import com.chandan.ecommerce.modal.User;

public interface UserService {
    User findUserByJwtToken(String jwt) throws Exception;
    User findUserByEmail(String email) throws Exception;
}
