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
public class SettingsRequest {

    @NotNull(message = "darkTheme is invalid!")
    private boolean darkTheme;

    @NotEmpty(message = "language is required!")
    @NotNull(message = "language is required!")
    @NotBlank(message = "language is required!")
    private String languageKey;
}
