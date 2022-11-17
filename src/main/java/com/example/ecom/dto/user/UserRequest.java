package com.example.ecom.dto.user;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.constant.TypeValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema
public class UserRequest {
    @NotNull(message = LanguageMessageKey.USERNAME_REQUIRED)
    @Pattern(regexp = TypeValidation.USERNAME, message = LanguageMessageKey.INVALID_USERNAME)
    private String username;

    @NotNull(message = LanguageMessageKey.GENDER_REQUIRED)
    @Min(value = 0, message = LanguageMessageKey.ONLY_0_1)
    @Max(value = 1, message = LanguageMessageKey.ONLY_0_1)
    private int gender;

    @NotNull(message = LanguageMessageKey.DOB_REQUIRED)
    @Pattern(regexp = TypeValidation.DATE, message = LanguageMessageKey.INVALID_DOB)
    private String dob;

    @NotEmpty(message = LanguageMessageKey.ADDRESS_REQUIRED)
    @NotBlank(message = LanguageMessageKey.ADDRESS_REQUIRED)
    @NotNull(message = LanguageMessageKey.ADDRESS_REQUIRED)
    private String address;

    @NotEmpty(message = LanguageMessageKey.FIRSTNAME_REQUIRED)
    @NotBlank(message = LanguageMessageKey.FIRSTNAME_REQUIRED)
    @NotNull(message = LanguageMessageKey.FIRSTNAME_REQUIRED)
    private String firstName;

    @NotEmpty(message = LanguageMessageKey.LASTNAME_REQUIRED)
    @NotBlank(message = LanguageMessageKey.LASTNAME_REQUIRED)
    @NotNull(message = LanguageMessageKey.LASTNAME_REQUIRED)
    private String lastName;

    @NotNull(message = LanguageMessageKey.EMAIL_REQUIRED)
    @Pattern(regexp = TypeValidation.EMAIL, message = LanguageMessageKey.INVALID_EMAIL)
    private String email;

    @NotNull(message = LanguageMessageKey.PHONE_REQUIRED)
    @Pattern(regexp = TypeValidation.PHONE, message = LanguageMessageKey.INVALID_PHONE)
    private String phone;

    @NotNull(message = LanguageMessageKey.DELETED_REQUIRED)
    @Min(value = 0, message = LanguageMessageKey.ONLY_0_1)
    @Max(value = 1, message = LanguageMessageKey.ONLY_0_1)
    private int deleted;
}
