package com.example.ecom.service;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.utils.ObjectValidator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractService<r> {
    @Autowired
    protected r repository;

    @Autowired
    protected Environment env;

    @Autowired
    protected ObjectValidator objectValidator;

    protected ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected <T> void validate(T request) {
        boolean isError = false;
        Map<String, String> errors = objectValidator.validateRequestThenReturnMessage(generateError(request.getClass()),
                request);
        for (Map.Entry<String, String> items : errors.entrySet()) {
            if (items.getValue().length() > 0) {
                isError = true;
                break;
            }
        }
        if (isError) {
            throw new InvalidRequestException(errors, LanguageMessageKey.INVALID_REQUEST);
        }
    }

    protected Map<String, String> generateError(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, String> result = new HashMap<>();
        for (Field field : fields) {
            result.put(field.getName(), "");
        }
        return result;
    }

//    protected String isPublic(String ownerId, String loginId, boolean skipAccessability) {
//        if (skipAccessability)
//            return "";
//        if (ownerId.compareTo(loginId) == 0) {
//            return "";
//        } else
//            return "public";
//    }
}
