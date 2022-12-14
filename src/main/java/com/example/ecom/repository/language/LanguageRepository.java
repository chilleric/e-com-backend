package com.example.ecom.repository.language;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LanguageRepository {
    Optional<List<Language>> getLanguages(Map<String, String> allParams, String keySort, int page,
            int pageSize, String sortField);

    void insertAndUpdate(Language language);

    long getTotal(Map<String, String> allParams);
}
