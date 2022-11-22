package com.example.ecom.inventory.langauge;

import com.example.ecom.inventory.AbstractInventory;
import com.example.ecom.repository.language.Language;
import com.example.ecom.repository.language.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

@Service
public class LanguageInventoryImpl extends AbstractInventory<LanguageRepository> implements LanguageInventory {
    @Override
    public Optional<Language> findLanguageById(String id) {
        List<Language> languages = repository.getLanguages(Map.ofEntries(entry("_id", id)), "", 0, 0, "").get();
        if (languages.size() == 0) return Optional.empty();
        return Optional.of(languages.get(0));
    }

    @Override
    public Optional<Language> findLanguageByKey(String key) {
        List<Language> languages = repository.getLanguages(Map.ofEntries(entry("key", key)), "", 0, 0, "").get();
        if (languages.size() == 0) return Optional.empty();
        return Optional.of(languages.get(0));
    }
}
