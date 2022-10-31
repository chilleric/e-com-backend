package com.example.ecom.dto.login;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotEmpty(message = "username is required")
    @NotBlank(message = "username is required")
    @NotNull(message = "username is required")
    private String username;

    @NotNull(message = "password is required")
    @NotEmpty(message = "password is required")
    @NotBlank(message = "password is required")
    private String password;
}
