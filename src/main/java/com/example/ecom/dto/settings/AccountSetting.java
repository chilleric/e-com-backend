package com.example.ecom.dto.settings;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.example.ecom.constant.TypeValidation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSetting {
    @NotNull(message = "Username is required!")
    @Pattern(regexp = TypeValidation.USERNAME, message = "Username is invalid!")
    private String username;

    @NotNull(message = "Gender is required!")
    @Min(value = 0, message = "Gender must be 0 or 1!")
    @Max(value = 1, message = "Gender must be 0 or 1!")
    private int gender;

    @NotNull(message = "Date of birth is required!")
    @Pattern(regexp = TypeValidation.DATE, message = "Date of birth is invalid!")
    private String dob;

    @NotEmpty(message = "Address is required!")
    @NotBlank(message = "Address is required!")
    @NotNull(message = "Address is required!")
    private String address;

    @NotNull(message = "Email is required!")
    @Pattern(regexp = TypeValidation.EMAIL, message = "Email is invalid!")
    private String email;

    @NotNull(message = "Phone is required!")
    @Pattern(regexp = TypeValidation.PHONE, message = "Phone is invalid!")
    private String phone;

    @NotNull(message = "verify2FA is invalid")
    private boolean verify2FA;

    @NotEmpty(message = "First name is required!")
    @NotBlank(message = "First name is required!")
    @NotNull(message = "First name is required!")
    private String firstName;

    @NotEmpty(message = "Last name is required!")
    @NotBlank(message = "Last name is required!")
    @NotNull(message = "Last name is required!")
    private String lastName;
}
