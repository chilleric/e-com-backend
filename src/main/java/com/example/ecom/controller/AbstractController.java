package com.example.ecom.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.exception.UnauthorizedException;
import com.example.ecom.jwt.JwtValidation;

public abstract class AbstractController<s> {
    @Autowired
    protected s service;

    @Autowired
    protected JwtValidation jwtValidation;

    protected <T> ResponseEntity<CommonResponse<T>> response(Optional<T> response, String successMessage) {
        return new ResponseEntity<>(new CommonResponse<>(true, response.get(), successMessage, HttpStatus.OK.value()),
                HttpStatus.OK);
    }

    protected void validateToken(HttpServletRequest request) {
        if (!jwtValidation.validateToken(request)) {
            throw new UnauthorizedException("Unauthorized!");
        }
    }
}
