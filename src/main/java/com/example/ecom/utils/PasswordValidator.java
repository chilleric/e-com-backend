package com.example.ecom.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import com.example.ecom.constant.TypeValidation;
import com.example.ecom.exception.InvalidRequestException;

public class PasswordValidator {

    public static void validatePassword(String password) {
        if (!Base64.isBase64(password)) {
            throw new InvalidRequestException("Password must be encoded!");
        } else {
            try {
                String decodedNewPassword = new String(Base64.decodeBase64(password));
                if (!decodedNewPassword.matches(TypeValidation.BASE64_REGEX)) {
                    throw new InvalidRequestException("Password must be encoded!");
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Password must be encoded!");
            } catch (IllegalStateException e) {
                throw new InvalidRequestException("Password must be encoded!");
            }
        }
    }

    public static void validateNewPassword(String newPassword) {
        if (Base64.isBase64(newPassword)) {
            try {
                String decodedNewPassword = new String(Base64.decodeBase64(newPassword));
                if (!decodedNewPassword.matches(TypeValidation.BASE64_REGEX)) {
                    throw new InvalidRequestException("Password must be encoded!");
                }
                if (!decodedNewPassword.matches(TypeValidation.PASSWORD)) {
                    throw new InvalidRequestException("Password must be passed condition!");
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidRequestException("Password must be encoded!");
            } catch (IllegalStateException e) {
                throw new InvalidRequestException("Password must be encoded!");
            }
        } else {
            throw new InvalidRequestException("Password must be encoded!");
        }
    }

}
