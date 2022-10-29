package com.example.ecom.annotations;

import java.lang.annotation.Annotation;

public class ValidateType implements IValidateType {

    @Override
    public Class<? extends Annotation> annotationType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String regex() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
