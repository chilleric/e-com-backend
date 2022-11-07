package com.example.ecom.utils;

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
public class ObjectValidator {
    // @Autowired
    // @Qualifier("validator")
    // LocalValidatorFactoryBean validatorFactory;

    LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();

    public <T> Map<String, String> validateRequestThenReturnMessage(Map<String, String> errorResult, T t) {
        Set<ConstraintViolation<T>> violations = validatorFactory.getValidator().validate(t);
        for (ConstraintViolation<T> violation : violations) {
            errorResult.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return errorResult;
    }
}
