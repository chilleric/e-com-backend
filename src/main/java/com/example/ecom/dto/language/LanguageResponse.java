package com.example.ecom.dto.language;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageResponse {
    private String id;
    private String language;
    private String key;
    private Map<String, String> dictionary;
}
