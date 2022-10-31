package com.example.ecom.dto.login;

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
public class RegisterRequest {
    @NotNull(message = "Username is required!")
    @Pattern(regexp = TypeValidation.USERNAME, message = "Username is invalid!")
    private String username;

    @NotNull(message = "Password is required!")
    @NotBlank(message = "Password is required!")
    @NotEmpty(message = "Password is required!")
    private String password;

    @NotNull(message = "First name is required!")
    @NotBlank(message = "First name is required!")
    @NotEmpty(message = "First name is required!")
    private String firstName;

    @NotNull(message = "Last name is required!")
    @NotBlank(message = "Last name is required!")
    @NotEmpty(message = "Last name is required!")
    private String lastName;

    @Min(value = 0, message = "Gender must be valid")
    @Max(value = 1, message = "Gender must be valid")
    @NotNull(message = "gender is required!")
    private int gender;

    @NotNull(message = "Date of birth is required!")
    @Pattern(regexp = TypeValidation.DATE, message = "Date of birth is invalid!")
    private String dob;

    @NotNull(message = "Phone number is required!")
    @Pattern(regexp = TypeValidation.PHONE, message = "Phone is invalid!")
    private String phone;

    @NotNull(message = "Email is required!")
    @Pattern(regexp = TypeValidation.EMAIL, message = "Email is invalid")
    private String email;

    @NotNull(message = "Address is required!")
    @NotBlank(message = "Address is required!")
    @NotEmpty(message = "Address is required!")
    private String address;

}
