package com.example.ecom.service.language;

import java.util.Map;
import java.util.Optional;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.language.LanguageRequest;
import com.example.ecom.dto.language.LanguageResponse;

public interface LanguageService {
    Optional<ListWrapperResponse<LanguageResponse>> getLanguages(Map<String, String> allParams, String keySort,
            int page,
            int pageSize, String sortField);

    void addNewLanguage(LanguageRequest languageRequest);

    void updateLanguage(LanguageRequest languageRequest, String id);
}
