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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "settings")
public class SettingsController extends AbstractController<SettingService> {

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-general-settings")
    public ResponseEntity<CommonResponse<SettingsResponse>> getGeneralSettings(HttpServletRequest request) {
        ValidationResult result = validateToken(request, false);
        return response(service.getSettingsByUserId(result.getLoginId()), LanguageMessageKey.SUCCESS);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "update-general-settings")
    public ResponseEntity<CommonResponse<String>> updateGeneralSettings(HttpServletRequest request,
                                                                        @RequestBody SettingsRequest settingsRequest) {
        ValidationResult result = validateToken(request, false);
        service.updateSettings(settingsRequest, result.getLoginId());
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_GENERAL_SETTINGS_SUCCESS,
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "update-account-settings")
    public ResponseEntity<CommonResponse<String>> updateAccountInformation(HttpServletRequest request,
                                                                           @RequestBody AccountSetting accountSetting) {
        ValidationResult result = validateToken(request, false);
        service.updateAccountInformation(accountSetting, result.getLoginId());
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_ACCOUNT_SETTINGS_SUCCESS,
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "update-password")
    public ResponseEntity<CommonResponse<String>> updatePassword(HttpServletRequest request,
                                                                 @RequestBody ChangePasswordRequest changePasswordRequest) {
        ValidationResult result = validateToken(request, false);
        service.updatePassword(changePasswordRequest, result.getLoginId());
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_PASSWORD_SUCCESS,
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }
}
