package com.example.ecom.inventory.langauge;

import java.util.Optional;
import com.example.ecom.repository.language.Language;

public interface LanguageInventory {
    Optional<Language> findLanguageById(String id);

    Optional<Language> findLanguageByKey(String key);
}
