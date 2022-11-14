package com.example.ecom.service.language;

import static java.util.Map.entry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.language.LanguageRequest;
import com.example.ecom.dto.language.LanguageResponse;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.repository.language.Language;
import com.example.ecom.repository.language.LanguageRepository;
import com.example.ecom.service.AbstractService;

@Service
public class LanguageServiceImpl extends AbstractService<LanguageRepository> implements LanguageService {

        @Override
        public Optional<ListWrapperResponse<LanguageResponse>> getLanguages(Map<String, String> allParams,
                        String keySort,
                        int page, int pageSize, String sortField) {
                List<Language> languages = repository.getLanguages(allParams, keySort, page, pageSize, sortField).get();
                return Optional
                                .of(new ListWrapperResponse<LanguageResponse>(
                                                languages.stream()
                                                                .map(lang -> new LanguageResponse(
                                                                                lang.get_id().toString(),
                                                                                lang.getLanguage(), lang.getKey(),
                                                                                lang.getDictionary()))
                                                                .collect(Collectors.toList()),
                                                page, pageSize, repository.getTotal(allParams)));
        }

        @Override
        public void addNewLanguage(LanguageRequest languageRequest) {
                validate(languageRequest);
                List<Language> languagesName = repository
                                .getLanguages(Map.ofEntries(
                                                entry("key", languageRequest.getKey().toLowerCase())), "", 0,
                                                0,
                                                "")
                                .get();
                if (languagesName.size() > 0) {
                        Map<String, String> error = generateError(LanguageRequest.class);
                        error.put("key", "Invalid config key");
                        throw new InvalidRequestException(error, "Invalid config key");
                }
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
                List<Language> languagesName = repository
                                .getLanguages(Map.ofEntries(
                                                entry("key", languageRequest.getKey().toLowerCase())), "", 0,
                                                0,
                                                "")
                                .get();
                if (languagesName.size() > 0) {
                        Map<String, String> error = generateError(LanguageRequest.class);
                        error.put("key", "Invalid config key");
                        throw new InvalidRequestException(error, "Invalid config key");
                }
                Language language = languages.get(0);
                language.setLanguage(languageRequest.getLanguage());
                language.setKey(languageRequest.getKey().toLowerCase());
                language.setDictionary(languageRequest.getDictionary());
                repository.insertAndUpdate(language);
        }

}
