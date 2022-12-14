package com.example.ecom.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.language.LanguageFileRequest;
import com.example.ecom.dto.language.LanguageRequest;
import com.example.ecom.dto.language.LanguageResponse;
import com.example.ecom.dto.language.SelectLanguage;
import com.example.ecom.service.language.LanguageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value = "language")
public class LanguageController extends AbstractController<LanguageService> {

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-language-list")
    public ResponseEntity<CommonResponse<ListWrapperResponse<LanguageResponse>>> getLanguages(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "asc") String keySort,
            @RequestParam(defaultValue = "modified") String sortField, HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        return response(service.getLanguages(allParams, keySort, page, pageSize, sortField),
                LanguageMessageKey.SUCCESS, result.getViewPoints()
                        .get(LanguageResponse.class.getSimpleName()),
                result.getEditable().get(LanguageResponse.class.getSimpleName()));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-language-select-list")
    public ResponseEntity<CommonResponse<List<SelectLanguage>>> getSelectLanguages(
            HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        return response(service.getSelectLanguage(), LanguageMessageKey.SUCCESS, result.getViewPoints()
                .get(LanguageResponse.class.getSimpleName()),
                result.getEditable().get(LanguageResponse.class.getSimpleName()));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-default-dictionary")
    public ResponseEntity<CommonResponse<Map<String, String>>> getDefaultDictionary(
            HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        return response(service.getDefaultValueSample(), LanguageMessageKey.SUCCESS,
                result.getViewPoints()
                        .get(LanguageResponse.class.getSimpleName()),
                result.getEditable().get(LanguageResponse.class.getSimpleName()));
    }

    @GetMapping(value = "get-language-by-key")
    public ResponseEntity<CommonResponse<LanguageResponse>> getLanguageByKey(
            @RequestParam("key") String key,
            HttpServletRequest request) {
        return response(service.getLanguageByKey(key), LanguageMessageKey.SUCCESS, new ArrayList<>(),
                new ArrayList<>());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "add-new-language")
    public ResponseEntity<CommonResponse<String>> addNewLanguage(
            @RequestBody LanguageRequest languageRequest,
            HttpServletRequest httpServletRequest) {
        ValidationResult result = validateToken(httpServletRequest);
        service.addNewLanguage(languageRequest);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.ADD_LANGUAGE_SUCCESS,
                        HttpStatus.OK.value(), result.getViewPoints()
                                .get(LanguageResponse.class.getSimpleName()),
                        result.getEditable().get(LanguageResponse.class.getSimpleName())),
                null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "add-new-dictionary")
    public ResponseEntity<CommonResponse<String>> addNewDictionary(
            @RequestBody Map<String, String> payload,
            HttpServletRequest httpServletRequest) {
        ValidationResult result = validateToken(httpServletRequest);
        service.addNewDictionary(payload);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.ADD_LANGUAGE_KEY_SUCCESS,
                        HttpStatus.OK.value(), result.getViewPoints()
                                .get(LanguageResponse.class.getSimpleName()),
                        result.getEditable().get(LanguageResponse.class.getSimpleName())),
                null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "delete-dictionary-key")
    public ResponseEntity<CommonResponse<String>> deleteKeyDictionary(
            @RequestParam("key") String keyDict,
            HttpServletRequest httpServletRequest) {
        ValidationResult result = validateToken(httpServletRequest);
        service.deleteDictionaryKey(keyDict);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.DELETE_LANGUAGE_KEY_SUCCESS,
                        HttpStatus.OK.value(), result.getViewPoints()
                                .get(LanguageResponse.class.getSimpleName()),
                        result.getEditable().get(LanguageResponse.class.getSimpleName())),
                null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "update-language")
    public ResponseEntity<CommonResponse<String>> updateLanguage(
            @RequestBody LanguageRequest languageRequest,
            @RequestParam("id") String languageId,
            HttpServletRequest httpServletRequest) {
        ValidationResult result = validateToken(httpServletRequest);
        service.updateLanguage(languageRequest, languageId);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_LANGUAGE_SUCCESS,
                        HttpStatus.OK.value(), result.getViewPoints()
                                .get(LanguageResponse.class.getSimpleName()),
                        result.getEditable().get(LanguageResponse.class.getSimpleName())),
                null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "update-dictionary-list")
    public ResponseEntity<CommonResponse<String>> updateLanguageByList(
            @RequestBody List<LanguageFileRequest> payload,
            HttpServletRequest httpServletRequest) {
        ValidationResult result = validateToken(httpServletRequest);
        service.updateDictionaryByFile(payload);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_LANGUAGE_SUCCESS,
                        HttpStatus.OK.value(), result.getViewPoints()
                                .get(LanguageResponse.class.getSimpleName()),
                        result.getEditable().get(LanguageResponse.class.getSimpleName())),
                null,
                HttpStatus.OK.value());
    }
}
