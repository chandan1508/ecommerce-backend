package com.chandan.ecommerce.request;

import com.chandan.ecommerce.domain.USER_ROLE;
import lombok.Data;

@Data
public class LoginOtpRequest {
    private String email;
    private String otp;
    private USER_ROLE role;
}
