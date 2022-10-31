package com.example.ecom.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.login.LoginRequest;
import com.example.ecom.dto.login.LoginResponse;
import com.example.ecom.dto.login.RegisterRequest;
import com.example.ecom.service.login.LoginService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value = "auth")
// @CrossOrigin(origins = "http://localhost:3000/", methods = {
// RequestMethod.OPTIONS, RequestMethod.GET,
// RequestMethod.POST, RequestMethod.PUT,
// RequestMethod.DELETE }, allowedHeaders = "*", allowCredentials = "true")
public class LoginController extends AbstractController<LoginService> {

    @PostMapping(value = "login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        return response(service.login(loginRequest, false), "Login successfully!");
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "logout")
    public ResponseEntity<CommonResponse<String>> logout(HttpServletRequest request,
            @RequestParam(required = true) String deviceId) {
        ValidationResult result = validateToken(request, false);
        service.logout(result.getLoginId(), deviceId);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Logout successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @PostMapping(value = "register")
    public ResponseEntity<CommonResponse<String>> register(@RequestBody RegisterRequest registerRequest) {
        service.register(registerRequest);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Verify email sended successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @PostMapping(value = "verify-email")
    public ResponseEntity<CommonResponse<String>> verifyEmail(@RequestParam(required = true) String email,
            @RequestParam(required = true) String code) {
        service.verifyRegister(code, email);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Verified account successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @PostMapping(value = "verify-email/resend")
    public ResponseEntity<CommonResponse<String>> resendVerifyEmail(@RequestParam(required = true) String email) {
        service.resendVerifyRegister(email);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Resend verify email successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @PostMapping(value = "forgot-password")
    public ResponseEntity<CommonResponse<String>> forgotPassword(@RequestParam(required = true) String email) {
        service.forgotPassword(email);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Forgot password email successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @PostMapping(value = "verify-2fa")
    public ResponseEntity<CommonResponse<LoginResponse>> verify2FA(@RequestParam(required = true) String email,
            @RequestParam(required = true) String code) {
        return response(service.verify2FA(email, code), "Verify 2FA successfully!");
    }

    @PostMapping(value = "verify-2fa/resend")
    public ResponseEntity<CommonResponse<String>> resendVerify2FA(@RequestParam(required = true) String email) {
        service.resend2FACode(email);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Resend verify 2FA email successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

}
