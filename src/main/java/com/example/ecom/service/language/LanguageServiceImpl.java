package com.example.ecom.service.language;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.language.LanguageRequest;
import com.example.ecom.dto.language.LanguageResponse;
import com.example.ecom.dto.language.SelectLanguage;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.repository.language.Language;
import com.example.ecom.repository.language.LanguageRepository;
import com.example.ecom.service.AbstractService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
public class LanguageServiceImpl extends AbstractService<LanguageRepository> implements LanguageService {
    @Override
    public Optional<ListWrapperResponse<LanguageResponse>> getLanguages(Map<String, String> allParams,
                                                                        String keySort, int page, int pageSize, String sortField) {
        List<Language> languages = repository.getLanguages(allParams, keySort, page, pageSize, sortField).get();
        return Optional.of(new ListWrapperResponse<LanguageResponse>(languages.stream()
                .map(lang -> new LanguageResponse(lang.get_id().toString(), lang.getLanguage(),
                        lang.getKey(), lang.getDictionary()))
                .collect(Collectors.toList()), page, pageSize, repository.getTotal(allParams)));
    }

    public void validateDictionary(Map<String, String> defaultValue, Map<String, String> inputValue) {
        if (defaultValue.size() != 0 && inputValue.size() == 0) {
            throw new InvalidRequestException(new HashMap<>(), "Must contain all key!");
        }
        for (Map.Entry<String, String> defaultItem : defaultValue.entrySet()) {
            boolean isFound = false;
            for (Map.Entry<String, String> inputItem : inputValue.entrySet()) {
                if (inputItem.getKey().compareTo(defaultItem.getKey()) == 0) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                throw new InvalidRequestException(new HashMap<>(), "Must contain all key!");
            }
        }
        for (Map.Entry<String, String> inputItem : defaultValue.entrySet()) {
            boolean isFound = false;
            for (Map.Entry<String, String> defaultItem : inputValue.entrySet()) {
                if (inputItem.getKey().compareTo(defaultItem.getKey()) == 0) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                throw new InvalidRequestException(new HashMap<>(), "More key then default!");
            }
        }
    }

    @Override
    public void addNewLanguage(LanguageRequest languageRequest) {
        validate(languageRequest);
        List<Language> languagesName = repository
                .getLanguages(Map.ofEntries(entry("key", languageRequest.getKey().toLowerCase())), "",
                        0, 0, "")
                .get();
        if (languagesName.size() > 0 || languageRequest.getKey().length() > 2) {
            Map<String, String> error = generateError(LanguageRequest.class);
            error.put("key", "Invalid config key");
            throw new InvalidRequestException(error, "Invalid config key");
        }
        List<Language> languageDefault = repository
                .getLanguages(Map.ofEntries(entry("key", "en")), "", 0, 0, "").get();
        validateDictionary(languageDefault.get(0).getDictionary(), languageRequest.getDictionary());
        Language language = objectMapper.convertValue(languageRequest, Language.class);
        language.setKey(language.getKey().toLowerCase());
        repository.insertAndUpdate(language);
    }

    @Override
    public void updateLanguage(LanguageRequest languageRequest, String id) {
        validate(languageRequest);
        List<Language> languages = repository.getLanguages(Map.ofEntries(entry("_id", id)), "", 0, 0, "").get();
        if (languages.size() == 0) {
            throw new ResourceNotFoundException("not found language");
        }
        Language language = languages.get(0);
        if (language.getKey().compareTo("en") != 0) {
            List<Language> languagesName = repository
                    .getLanguages(Map.ofEntries(entry("key", languageRequest.getKey().toLowerCase())), "",
                            0, 0, "")
                    .get();
            if (languageRequest.getKey().length() != 2) {
                Map<String, String> error = generateError(LanguageRequest.class);
                error.put("key", "Invalid config key");
                throw new InvalidRequestException(error, "Key is 2 digits char!");
            }
            if (languagesName.size() > 0) {
                if (languagesName.get(0).get_id().compareTo(languages.get(0).get_id()) != 0) {
                    Map<String, String> error = generateError(LanguageRequest.class);
                    error.put("key", "Invalid config key");
                    throw new InvalidRequestException(error, "Invalid config key");
                }
            }
            if (languages.get(0).getKey().compareTo("en") != 0) {
                List<Language> languageDefault = repository
                        .getLanguages(Map.ofEntries(entry("key", "en")), "", 0, 0, "").get();
                validateDictionary(languageDefault.get(0).getDictionary(), languageRequest.getDictionary());
            }
            language.setLanguage(languageRequest.getLanguage());
            language.setKey(languageRequest.getKey().toLowerCase());
            language.setDictionary(languageRequest.getDictionary());
        } else {
            language.setDictionary(languageRequest.getDictionary());
        }
        repository.insertAndUpdate(language);
    }

    @Override
    public Optional<Map<String, String>> getDefaultValueSample() {
        List<Language> languageDefault = repository
                .getLanguages(Map.ofEntries(entry("key", "en")), "", 0, 0, "").get();
        return Optional.of(languageDefault.get(0).getDictionary());
    }

    @Override
    public Optional<List<SelectLanguage>> getSelectLanguage() {
        List<Language> languages = repository.getLanguages(new HashMap<>(), "", 0, 0, "").get();
        if (languages.size() == 0) {
            return Optional.of(new ArrayList<>());
        }
        return Optional.of(languages.stream().map(lang -> new SelectLanguage(lang.getLanguage(), lang.getKey()))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<LanguageResponse> getLanguageByKey(String key) {
        List<Language> languages = repository
                .getLanguages(Map.ofEntries(entry("key", key.toLowerCase())), "", 0, 0, "").get();
        List<Language> languageDefault = repository
                .getLanguages(Map.ofEntries(entry("key", "en")), "", 0, 0, "").get();
        if (languages.size() == 0) {
            return Optional.of(new LanguageResponse(languageDefault.get(0).get_id().toString(),
                    languageDefault.get(0).getLanguage(), languageDefault.get(0).getKey(),
                    languageDefault.get(0).getDictionary()));
        } else {
            return Optional.of(new LanguageResponse(languages.get(0).get_id().toString(),
                    languages.get(0).getLanguage(), languages.get(0).getKey(),
                    languages.get(0).getDictionary()));
        }
    }
}
