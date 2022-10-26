package com.example.ecom.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    protected BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected <T> void validate(T request) {
        String message = objectValidator.validateRequestThenReturnMessage(request);
        if (!ObjectUtils.isEmpty(message)) {
            throw new InvalidRequestException(message);
        }
    }
}
