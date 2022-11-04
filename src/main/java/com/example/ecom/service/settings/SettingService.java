package com.example.ecom.service.settings;

import java.util.Optional;

import com.example.ecom.dto.settings.AccountSetting;
import com.example.ecom.dto.settings.ChangePasswordRequest;
import com.example.ecom.dto.settings.SettingsRequest;
import com.example.ecom.dto.settings.SettingsResponse;

public interface SettingService {
    Optional<SettingsResponse> getSettingsByUserId(String userId);

    void updateSettings(SettingsRequest settingsRequest, String userId);

    void updatePassword(ChangePasswordRequest changePasswordRequest, String userId);

    void updateAccountInformation(AccountSetting accountSetting, String userId);
}
