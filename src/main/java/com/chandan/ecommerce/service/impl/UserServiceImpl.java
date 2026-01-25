package com.chandan.ecommerce.service.impl;

import com.chandan.ecommerce.config.JwtProvider;
import com.chandan.ecommerce.modal.User;
import com.chandan.ecommerce.repository.UserRepository;
import com.chandan.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;


    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        String email=jwtProvider.getEmailFromJwtToken(jwt);

        User user=this.findUserByEmail(email);
        if(user==null){
            throw new Exception("user not found with email - " + email);
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user=userRepository.findByEmail(email);
        if(user==null){
            throw new Exception("user not found with email - "+email);
        }
        return user;
    }
}
