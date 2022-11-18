package com.example.ecom.service.language;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.language.LanguageFileRequest;
import com.example.ecom.dto.language.LanguageRequest;
import com.example.ecom.dto.language.LanguageResponse;
import com.example.ecom.dto.language.SelectLanguage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LanguageService {
    Optional<ListWrapperResponse<LanguageResponse>> getLanguages(Map<String, String> allParams, String keySort,
                                                                 int page,
                                                                 int pageSize, String sortField);

    Optional<LanguageResponse> getLanguageByKey(String key);

    Optional<List<SelectLanguage>> getSelectLanguage();

    void addNewLanguage(LanguageRequest languageRequest);

    void updateLanguage(LanguageRequest languageRequest, String id);

    void deleteDictionaryKey(String dictKey);

    void addNewDictionary(Map<String, String> newDict);

    Optional<Map<String, String>> getDefaultValueSample();

    void updateDictionaryByFile(List<LanguageFileRequest> payload);
}
