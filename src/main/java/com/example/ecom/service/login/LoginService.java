package com.example.ecom.service.login;

import java.util.Optional;
import com.example.ecom.dto.login.LoginRequest;
import com.example.ecom.dto.login.LoginResponse;
import com.example.ecom.dto.login.RegisterRequest;

public interface LoginService {
    Optional<LoginResponse> login(LoginRequest loginRequest, boolean isRegister);

    void logout(String id, String deviceId);

    void register(RegisterRequest registerRequest);

    void verifyRegister(String code, String email);

    void resendVerifyRegister(String email);

    void forgotPassword(String email);

    Optional<LoginResponse> verify2FA(String email, String code);

    void resend2FACode(String email);
}
