package com.example.ecom.dto.language;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRequest {
    @NotEmpty(message = "Language is required")
    @NotBlank(message = "Language is required")
    @NotNull(message = "Language is required")
    private String language;

    @NotEmpty(message = "Key is required")
    @NotBlank(message = "Key is required")
    @NotNull(message = "Key is required")
    private String key;

    private Map<String, String> dictionary;
}
