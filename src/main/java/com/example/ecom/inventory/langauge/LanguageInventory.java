package com.example.ecom.inventory.langauge;

import com.example.ecom.repository.language.Language;

import java.util.Optional;

public interface LanguageInventory {
    Optional<Language> findLanguageById(String id);

    Optional<Language> findLanguageByKey(String key);
}
