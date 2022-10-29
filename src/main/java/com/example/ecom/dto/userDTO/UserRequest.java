package com.example.ecom.dto.userDTO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.example.ecom.constant.TypeValidation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema
public class UserRequest {
    @NotEmpty(message = "Username is required!")
    @NotBlank(message = "Username is required!")
    @NotNull(message = "Username is required!")
    @Pattern(regexp = TypeValidation.USERNAME, message = "Username is invalid!")
    private String username;

    @NotEmpty(message = "Password is required!")
    @NotBlank(message = "Password is required!")
    @NotNull(message = "Password is required!")
    @Pattern(regexp = TypeValidation.PASSWORD, message = "Password is invalid!")
    private String password;

    @NotNull(message = "Gender is required!")
    @Max(1)
    @Min(0)
    @Pattern(regexp = TypeValidation.GENDER, message = "Gender is invalid!")
    private int gender;

    @NotEmpty(message = "Date of birth is required!")
    @NotBlank(message = "Date of birth is required!")
    @NotNull(message = "Date of birth is required!")
    @Pattern(regexp = TypeValidation.DATE, message = "Date of birth is invalid!")
    private String dob;

    @NotEmpty(message = "Address is required!")
    @NotBlank(message = "Address is required!")
    @NotNull(message = "Address is required!")
    private String address;
    
    @NotEmpty(message = "First name is required!")
    @NotBlank(message = "First name is required!")
    @NotNull(message = "First name is required!")
    private String firstName;
    
    @NotEmpty(message = "Last name is required!")
    @NotBlank(message = "Last name is required!")
    @NotNull(message = "Last name is required!")
    private String lastName;
    
    @NotEmpty(message = "Email is required!")
    @NotBlank(message = "Email is required!")
    @NotNull(message = "Email is required!")
    @Pattern(regexp = TypeValidation.EMAIL, message = "Email is invalid!")
    private String email;

    @NotEmpty(message = "Phone is required!")
    @NotBlank(message = "Phone is required!")
    @NotNull(message = "Phone is required!")
    @Pattern(regexp = TypeValidation.PHONE, message = "Phone is invalid!")
    private String phone;

    @Min(value = 0)
    @Max(value = 1)
    @NotNull(message = "Deleted is required!")
    @Pattern(regexp = TypeValidation.GENDER, message = "Deleted is invalid!")
    private int deleted;
}
