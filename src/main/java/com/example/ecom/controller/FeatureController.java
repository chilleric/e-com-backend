package com.example.ecom.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.feature.FeatureResponse;
import com.example.ecom.service.feature.FeatureService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value = "feature")
public class FeatureController extends AbstractController<FeatureService> {

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-list-feature")
    public ResponseEntity<CommonResponse<ListWrapperResponse<FeatureResponse>>> getFeatures(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "asc") String keySort,
            @RequestParam(defaultValue = "modified") String sortField, HttpServletRequest request) {
        return response(service.getFeatures(allParams, keySort, page, pageSize, sortField),
                "Get list of features successfully!");
    }
}
