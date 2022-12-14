package com.example.ecom.dto.settings;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import com.example.ecom.constant.LanguageMessageKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingsRequest {

    @NotNull(message = LanguageMessageKey.INVALID_DARK_THEME)
    private boolean darkTheme;

    @NotEmpty(message = LanguageMessageKey.LANGUAGE_KEY_REQUIRED)
    @NotNull(message = LanguageMessageKey.LANGUAGE_KEY_REQUIRED)
    @NotBlank(message = LanguageMessageKey.LANGUAGE_KEY_REQUIRED)
    private String languageKey;
}
