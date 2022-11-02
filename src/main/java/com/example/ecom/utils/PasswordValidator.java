package com.example.ecom.utils;

import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;

import com.example.ecom.constant.TypeValidation;
import com.example.ecom.exception.InvalidRequestException;

public class PasswordValidator {

    public static void validatePassword(Map<String, String> errorObject, String password) {
        if (!Base64.isBase64(password)) {
            errorObject.put("password", "Password must be encoded!");
            throw new InvalidRequestException(errorObject, "Password must be encoded!");
        } else {
            try {
                String decodedNewPassword = new String(Base64.decodeBase64(password));
                if (!decodedNewPassword.matches(TypeValidation.BASE64_REGEX)) {
                    errorObject.put("password", "Password must be encoded!");
                    throw new InvalidRequestException(errorObject, "Password must be encoded!");
                }
            } catch (IllegalArgumentException e) {
                errorObject.put("password", "Password must be encoded!");
                throw new InvalidRequestException(errorObject, "Password must be encoded!");
            } catch (IllegalStateException e) {
                errorObject.put("password", "Password must be encoded!");
                throw new InvalidRequestException(errorObject, "Password must be encoded!");
            }
        }
    }

    public static void validateNewPassword(Map<String, String> errorObject, String newPassword) {
        if (Base64.isBase64(newPassword)) {
            try {
                String decodedNewPassword = new String(Base64.decodeBase64(newPassword));
                if (!decodedNewPassword.matches(TypeValidation.BASE64_REGEX)) {
                    errorObject.put("password", "Password must be encoded!");
                    throw new InvalidRequestException(errorObject, "Password must be encoded!");
                }
                if (!decodedNewPassword.matches(TypeValidation.PASSWORD)) {
                    errorObject.put("password", "Password must be passed condition!");
                    throw new InvalidRequestException(errorObject, "Password must be passed condition!");
                }
            } catch (IllegalArgumentException e) {
                errorObject.put("password", "Password must be encoded!");
                throw new InvalidRequestException(errorObject, "Password must be encoded!");
            } catch (IllegalStateException e) {
                throw new InvalidRequestException(errorObject, "Password must be encoded!");
            }
        } else {
            errorObject.put("password", "Password must be encoded!");
            throw new InvalidRequestException(errorObject, "Password must be encoded!");
        }
    }

}
