package com.example.ecom.dto.language;

import com.example.ecom.constant.LanguageMessageKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRequest {
    @NotEmpty(message = LanguageMessageKey.LANGUAGE_REQUIRED)
    @NotBlank(message = LanguageMessageKey.LANGUAGE_REQUIRED)
    @NotNull(message = LanguageMessageKey.LANGUAGE_REQUIRED)
    private String language;

    @NotEmpty(message = LanguageMessageKey.LANGUAGE_KEY_REQUIRED)
    @NotBlank(message = LanguageMessageKey.LANGUAGE_KEY_REQUIRED)
    @NotNull(message = LanguageMessageKey.LANGUAGE_KEY_REQUIRED)
    private String key;

    private Map<String, String> dictionary;
}
