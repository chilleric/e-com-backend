package com.example.ecom.service.language;

import static java.util.Map.entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.language.LanguageFileRequest;
import com.example.ecom.dto.language.LanguageRequest;
import com.example.ecom.dto.language.LanguageResponse;
import com.example.ecom.dto.language.SelectLanguage;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.inventory.langauge.LanguageInventory;
import com.example.ecom.repository.language.Language;
import com.example.ecom.repository.language.LanguageRepository;
import com.example.ecom.service.AbstractService;

@Service
public class LanguageServiceImpl extends AbstractService<LanguageRepository>
        implements LanguageService {

    @Autowired
    private LanguageInventory languageInventory;

    @Override
    public Optional<ListWrapperResponse<LanguageResponse>> getLanguages(
            Map<String, String> allParams, String keySort, int page, int pageSize,
            String sortField) {
        List<Language> languages =
                repository.getLanguages(allParams, keySort, page, pageSize, sortField).get();
        return Optional.of(new ListWrapperResponse<LanguageResponse>(
                languages.stream()
                        .map(lang -> new LanguageResponse(lang.get_id().toString(),
                                lang.getLanguage(), lang.getKey(), lang.getDictionary()))
                        .collect(Collectors.toList()),
                page, pageSize, repository.getTotal(allParams)));
    }

    public void validateDictionary(Map<String, String> defaultValue,
            Map<String, String> inputValue) {
        if (defaultValue.size() != 0 && inputValue.size() == 0) {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.CONTAIN_ALL_KEY);
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
                throw new InvalidRequestException(new HashMap<>(),
                        LanguageMessageKey.CONTAIN_ALL_KEY);
            }
        }
        for (Map.Entry<String, String> inputItem : inputValue.entrySet()) {
            boolean isFound = false;
            for (Map.Entry<String, String> defaultItem : defaultValue.entrySet()) {
                if (inputItem.getKey().compareTo(defaultItem.getKey()) == 0) {
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                throw new InvalidRequestException(new HashMap<>(),
                        LanguageMessageKey.CONTAIN_ALL_KEY);
            }
        }
    }

    @Override
    public void addNewLanguage(LanguageRequest languageRequest) {
        validate(languageRequest);
        Map<String, String> error = generateError(LanguageRequest.class);
        languageInventory.findLanguageByKey(languageRequest.getKey().toLowerCase())
                .ifPresent(language -> {
                    error.put("key", LanguageMessageKey.INVALID_LANGUAGE_KEY);
                    throw new InvalidRequestException(error,
                            LanguageMessageKey.INVALID_LANGUAGE_KEY);
                });
        if (languageRequest.getKey().length() != 2) {
            error.put("key", LanguageMessageKey.INVALID_LANGUAGE_KEY);
            throw new InvalidRequestException(error, LanguageMessageKey.INVALID_LANGUAGE_KEY);
        }
        Language languageDefault = languageInventory.findLanguageByKey("en")
                .orElseThrow(() -> new InvalidRequestException(new HashMap<>(),
                        LanguageMessageKey.LANGUAGE_NOT_FOUND));
        Language language = new Language(null, languageRequest.getLanguage(),
                languageRequest.getKey().toLowerCase(), languageDefault.getDictionary());
        repository.insertAndUpdate(language);
    }

    @Override
    public void updateLanguage(LanguageRequest languageRequest, String id) {
        validate(languageRequest);
        Language language = languageInventory.findLanguageById(id).orElseThrow(
                () -> new ResourceNotFoundException(LanguageMessageKey.LANGUAGE_NOT_FOUND));
        Language languageDefault = languageInventory.findLanguageByKey("en").orElseThrow(
                () -> new ResourceNotFoundException(LanguageMessageKey.LANGUAGE_NOT_FOUND));
        validateDictionary(languageDefault.getDictionary(), languageRequest.getDictionary());
        Map<String, String> error = generateError(LanguageRequest.class);
        if (language.getKey().compareTo("en") != 0) {
            if (languageRequest.getKey().length() != 2) {
                error.put("key", LanguageMessageKey.INVALID_LANGUAGE_KEY);
                throw new InvalidRequestException(error, LanguageMessageKey.INVALID_KEY_2_DIGIT);
            }
            languageInventory.findLanguageByKey(languageRequest.getKey().toLowerCase())
                    .ifPresent(languageName -> {
                        if (languageName.get_id().compareTo(language.get_id()) != 0) {
                            error.put("key", LanguageMessageKey.INVALID_LANGUAGE_KEY);
                            throw new InvalidRequestException(error,
                                    LanguageMessageKey.INVALID_LANGUAGE_KEY);
                        }
                    });
            language.setLanguage(languageRequest.getLanguage());
            language.setKey(languageRequest.getKey().toLowerCase());
            language.setDictionary(languageRequest.getDictionary());
        } else {
            language.setDictionary(languageRequest.getDictionary());
        }
        repository.insertAndUpdate(language);
    }

    @Override
    public void deleteDictionaryKey(String dictKey) {
        repository.getLanguages(new HashMap<>(), "", 0, 0, "").get().forEach(language -> {
            Map<String, String> updateDict = new HashMap<>();
            language.getDictionary().entrySet().forEach(word -> {
                if (word.getKey().compareTo(dictKey) != 0) {
                    updateDict.put(word.getKey(), word.getValue());
                }
            });
            language.setDictionary(updateDict);
            repository.insertAndUpdate(language);
        });
    }

    @Override
    public void addNewDictionary(Map<String, String> newDict) {
        StringBuilder keyUpdate = new StringBuilder();
        for (Map.Entry<String, String> key : newDict.entrySet()) {
            boolean isHasKey = false;
            if (key.getKey().compareTo("key") == 0) {
                isHasKey = true;
                keyUpdate.append(key.getValue());
            }
            if (!isHasKey) {
                throw new InvalidRequestException(new HashMap<>(),
                        LanguageMessageKey.INVALID_LANGUAGE_KEY);
            }
            if (isHasKey) {
                break;
            }
        }
        repository.getLanguages(new HashMap<>(), "", 0, 0, "").get().forEach(lang -> {
            if (!newDict.containsKey(lang.getKey())) {
                throw new InvalidRequestException(new HashMap<>(),
                        LanguageMessageKey.INVALID_CONTAIN_ALL_LANGUAGE);
            }
        });
        repository.getLanguages(new HashMap<>(), "", 0, 0, "").get().forEach(lang -> {
            if (newDict.containsKey(lang.getKey())) {
                Map<String, String> updateDict = lang.getDictionary();
                updateDict.put(keyUpdate.toString(), newDict.get(lang.getKey()));
                lang.setDictionary(updateDict);
                repository.insertAndUpdate(lang);
            }
        });
    }

    @Override
    public Optional<Map<String, String>> getDefaultValueSample() {
        Language languageDefault = languageInventory.findLanguageByKey("en").orElseThrow(
                () -> new ResourceNotFoundException(LanguageMessageKey.LANGUAGE_NOT_FOUND));
        return Optional.of(languageDefault.getDictionary());
    }

    @Override
    public void updateDictionaryByFile(List<LanguageFileRequest> payload) {
        payload.forEach(item -> {
            if (StringUtils.hasText(item.getId())) {
                languageInventory.findLanguageById(item.getId()).ifPresent(langUpdate -> {
                    langUpdate.setDictionary(item.getDictionary());
                    repository.insertAndUpdate(langUpdate);
                });
            }
        });
    }

    @Override
    public Optional<List<SelectLanguage>> getSelectLanguage() {
        List<Language> languages = repository.getLanguages(new HashMap<>(), "", 0, 0, "").get();
        if (languages.size() == 0) {
            return Optional.of(new ArrayList<>());
        }
        return Optional.of(languages.stream()
                .map(lang -> new SelectLanguage(lang.getLanguage(), lang.getKey()))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<LanguageResponse> getLanguageByKey(String key) {
        List<Language> languages = repository
                .getLanguages(Map.ofEntries(entry("key", key.toLowerCase())), "", 0, 0, "").get();
        List<Language> languageDefault =
                repository.getLanguages(Map.ofEntries(entry("key", "en")), "", 0, 0, "").get();
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
