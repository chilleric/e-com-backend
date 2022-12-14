package com.example.ecom.repository.code;

public enum TypeCode {

    REGISTER("register"), VERIFY2FA("verify2FA");

    private String responseType;

    TypeCode(String type) {
        this.responseType = type;
    }

    public String getResponseType() {
        return responseType;
    }
}
