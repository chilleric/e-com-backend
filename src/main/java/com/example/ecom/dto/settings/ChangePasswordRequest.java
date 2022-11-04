package com.example.ecom.dto.settings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotEmpty(message = "Old password is required!")
    @NotBlank(message = "Old password is required!")
    @NotNull(message = "Old password is required!")
    private String oldPassword;

    @NotEmpty(message = "New password is required!")
    @NotBlank(message = "New password is required!")
    @NotNull(message = "New password is required!")
    private String newPassword;

    @NotEmpty(message = "Confirm password is required!")
    @NotBlank(message = "Confirm password is required!")
    @NotNull(message = "Confirm password is required!")
    private String confirmNewPassword;
}
