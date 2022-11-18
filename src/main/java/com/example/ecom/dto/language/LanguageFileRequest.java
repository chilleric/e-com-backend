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
public class LanguageFileRequest {
    @NotNull(message = LanguageMessageKey.ID_REQUIRED)
    @NotEmpty(message = LanguageMessageKey.ID_REQUIRED)
    @NotBlank(message = LanguageMessageKey.ID_REQUIRED)
    private String id;

    private Map<String, String> dictionary;
}
