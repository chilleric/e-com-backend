package com.example.ecom.controller;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.settings.AccountSetting;
import com.example.ecom.dto.settings.ChangePasswordRequest;
import com.example.ecom.dto.settings.SettingsRequest;
import com.example.ecom.dto.settings.SettingsResponse;
import com.example.ecom.service.settings.SettingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "settings")
public class SettingsController extends AbstractController<SettingService> {

  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping(value = "get-general-settings")
  public ResponseEntity<CommonResponse<SettingsResponse>> getGeneralSettings(
      HttpServletRequest request) {
    ValidationResult result = validateToken(request);
    return response(service.getSettingsByUserId(result.getLoginId()), LanguageMessageKey.SUCCESS,
        result.getViewPoints()
            .get(SettingsResponse.class.getSimpleName()), result.getEditable()
            .get(SettingsResponse.class.getSimpleName()));
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping(value = "update-general-settings")
  public ResponseEntity<CommonResponse<String>> updateGeneralSettings(HttpServletRequest request,
      @RequestBody SettingsRequest settingsRequest) {
    ValidationResult result = validateToken(request);
    service.updateSettings(settingsRequest, result.getLoginId());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_GENERAL_SETTINGS_SUCCESS,
            HttpStatus.OK.value(), result.getViewPoints()
            .get(SettingsResponse.class.getSimpleName()), result.getEditable()
            .get(SettingsResponse.class.getSimpleName())),
        null,
        HttpStatus.OK.value());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping(value = "update-account-settings")
  public ResponseEntity<CommonResponse<String>> updateAccountInformation(HttpServletRequest request,
      @RequestBody AccountSetting accountSetting) {
    ValidationResult result = validateToken(request);
    service.updateAccountInformation(accountSetting, result.getLoginId());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_ACCOUNT_SETTINGS_SUCCESS,
            HttpStatus.OK.value(), result.getViewPoints()
            .get(SettingsResponse.class.getSimpleName()), result.getEditable()
            .get(SettingsResponse.class.getSimpleName())),
        null,
        HttpStatus.OK.value());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping(value = "update-password")
  public ResponseEntity<CommonResponse<String>> updatePassword(HttpServletRequest request,
      @RequestBody ChangePasswordRequest changePasswordRequest) {
    ValidationResult result = validateToken(request);
    service.updatePassword(changePasswordRequest, result.getLoginId());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_PASSWORD_SUCCESS,
            HttpStatus.OK.value(), result.getViewPoints()
            .get(SettingsResponse.class.getSimpleName()), result.getEditable()
            .get(SettingsResponse.class.getSimpleName())),
        null,
        HttpStatus.OK.value());
  }
}
