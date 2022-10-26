package com.example.ecom.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
public class ObjectValidator {
    @Autowired
    @Qualifier("validator")
    LocalValidatorFactoryBean validatorFactory;

    public <T> String validateRequestThenReturnMessage(T t) {
        Set<ConstraintViolation<T>> violations = validatorFactory.getValidator().validate(t);
        List<String> messages = new ArrayList<>();
        for (ConstraintViolation<T> violation : violations) {
            messages.add(violation.getMessage());
        }
        if (!messages.isEmpty())
            return messages.get(0);
        return "";
    }
}
