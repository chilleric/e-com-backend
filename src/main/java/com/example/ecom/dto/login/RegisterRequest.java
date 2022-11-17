package com.example.ecom.dto.login;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.constant.TypeValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message = LanguageMessageKey.USERNAME_REQUIRED)
    @Pattern(regexp = TypeValidation.USERNAME, message = LanguageMessageKey.INVALID_USERNAME)
    private String username;

    @NotNull(message = LanguageMessageKey.PASSWORD_REQUIRED)
    @NotBlank(message = LanguageMessageKey.PASSWORD_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.PASSWORD_REQUIRED)
    private String password;

    @NotNull(message = LanguageMessageKey.FIRSTNAME_REQUIRED)
    @NotBlank(message = LanguageMessageKey.FIRSTNAME_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.FIRSTNAME_REQUIRED)
    private String firstName;

    @NotNull(message = LanguageMessageKey.LASTNAME_REQUIRED)
    @NotBlank(message = LanguageMessageKey.LASTNAME_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.LASTNAME_REQUIRED)
    private String lastName;

    @NotNull(message = LanguageMessageKey.PHONE_REQUIRED)
    @Pattern(regexp = TypeValidation.PHONE, message = LanguageMessageKey.INVALID_PHONE)
    private String phone;

    @NotNull(message = LanguageMessageKey.EMAIL_REQUIRED)
    @Pattern(regexp = TypeValidation.EMAIL, message = LanguageMessageKey.INVALID_EMAIL)
    private String email;

    @NotNull(message = LanguageMessageKey.ADDRESS_REQUIRED)
    @NotBlank(message = LanguageMessageKey.ADDRESS_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.ADDRESS_REQUIRED)
    private String address;

}
