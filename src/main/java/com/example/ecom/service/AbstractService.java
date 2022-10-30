package com.example.ecom.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.ecom.constant.TypeValidation;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;
import com.example.ecom.utils.ObjectValidator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractService<r> {
    @Autowired
    protected r repository;

    @Autowired
    protected Environment env;

    @Autowired
    protected ObjectValidator objectValidator;

    protected ObjectMapper objectMapper;

    protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);;

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected void validatePassword(String password, boolean isNew) {
        if (!Base64.isBase64(password)) {
            throw new InvalidRequestException("Password must be encoded!");
        } else {
            try {
                String decodedNewPassword = new String(Base64.decodeBase64(password));
                if (!decodedNewPassword.matches(TypeValidation.BASE64_REGEX)) {
                    throw new InvalidRequestException("Password must be encoded!");
                }
                if (isNew && !decodedNewPassword.matches(TypeValidation.PASSWORD)) {
                    throw new InvalidRequestException("Password must be valid!");
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Password must be encoded!");
            } catch (IllegalStateException e) {
                throw new InvalidRequestException("Password must be encoded!");
            }
        }
    }

    protected BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected <T> void validate(T request) {
        String message = objectValidator.validateRequestThenReturnMessage(request);
        if (!ObjectUtils.isEmpty(message)) {
            throw new InvalidRequestException(message);
        }
    }

    protected String isPublic(String ownerId, String loginId, boolean skipAccessability) {
        if (skipAccessability)
            return "";
        if (ownerId.compareTo(loginId) == 0) {
            return "";
        } else
            return "public";
    }
}
