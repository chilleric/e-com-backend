package com.example.ecom.service.language;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.language.LanguageRequest;
import com.example.ecom.dto.language.LanguageResponse;
import com.example.ecom.dto.language.SelectLanguage;

public interface LanguageService {
    Optional<ListWrapperResponse<LanguageResponse>> getLanguages(Map<String, String> allParams, String keySort,
            int page,
            int pageSize, String sortField);

    Optional<LanguageResponse> getLanguageByKey(String key);

    Optional<List<SelectLanguage>> getSelectLanguage();

    void addNewLanguage(LanguageRequest languageRequest);

    void updateLanguage(LanguageRequest languageRequest, String id);
}
