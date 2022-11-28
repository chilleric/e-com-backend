package com.example.ecom.controller;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.feature.FeatureResponse;
import com.example.ecom.service.feature.FeatureService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "feature")
public class FeatureController extends AbstractController<FeatureService> {

  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping(value = "get-list-feature")
  public ResponseEntity<CommonResponse<ListWrapperResponse<FeatureResponse>>> getListsFeatures(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "10") int pageSize,
      @RequestParam Map<String, String> allParams,
      @RequestParam(defaultValue = "asc") String keySort,
      @RequestParam(defaultValue = "modified") String sortField, HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    return response(service.getFeatures(allParams, keySort, page, pageSize, sortField),
        LanguageMessageKey.SUCCESS,
        result.getViewPoints().get(FeatureResponse.class.getSimpleName()));
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping(value = "change-status")
  public ResponseEntity<CommonResponse<String>> updateStatusFeature(HttpServletRequest request,
      @RequestParam String id) {
    ValidationResult result = validateToken(request, false);
    service.changeStatusFeature(id);
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_FEATURE_SUCCESS,
            HttpStatus.OK.value(),
            result.getViewPoints().get(FeatureResponse.class.getSimpleName())),
        null,
        HttpStatus.OK.value());
  }
}
