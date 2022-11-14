package com.example.ecom.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
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
                validateToken(request, false);
                return response(service.getLanguages(allParams, keySort, page, pageSize, sortField), "Success");
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @GetMapping(value = "get-language-select-list")
        public ResponseEntity<CommonResponse<List<SelectLanguage>>> getSelectLanguages(HttpServletRequest request) {
                validateToken(request, false);
                return response(service.getSelectLanguage(), "Success");
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PostMapping(value = "add-new-language")
        public ResponseEntity<CommonResponse<String>> addNewLanguage(@RequestBody LanguageRequest languageRequest,
                        HttpServletRequest httpServletRequest) {
                validateToken(httpServletRequest, false);
                service.addNewLanguage(languageRequest);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, "Add language successfully!",
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PostMapping(value = "update-language")
        public ResponseEntity<CommonResponse<String>> updateLanguage(@RequestBody LanguageRequest languageRequest,
                        @RequestParam("id") String languageId,
                        HttpServletRequest httpServletRequest) {
                validateToken(httpServletRequest, false);
                service.updateLanguage(languageRequest, languageId);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, "Update language successfully!",
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }
}
