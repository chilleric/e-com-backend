package com.example.ecom.dto.login;

import com.example.ecom.constant.LanguageMessageKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotEmpty(message = LanguageMessageKey.USERNAME_REQUIRED)
    @NotBlank(message = LanguageMessageKey.USERNAME_REQUIRED)
    @NotNull(message = LanguageMessageKey.USERNAME_REQUIRED)
    private String username;

    @NotNull(message = LanguageMessageKey.PASSWORD_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.PASSWORD_REQUIRED)
    @NotBlank(message = LanguageMessageKey.PASSWORD_REQUIRED)
    private String password;
}
