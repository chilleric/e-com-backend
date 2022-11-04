package com.example.ecom.service.settings;

import static java.util.Map.entry;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.example.ecom.dto.settings.AccountSetting;
import com.example.ecom.dto.settings.ChangePasswordRequest;
import com.example.ecom.dto.settings.SettingsRequest;
import com.example.ecom.dto.settings.SettingsResponse;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.repository.settings.Setting;
import com.example.ecom.repository.settings.SettingRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.PasswordValidator;

@Service
public class SettingServiceImpl extends AbstractService<SettingRepository> implements SettingService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<SettingsResponse> getSettingsByUserId(String userId) {
        List<Setting> settings = repository.getSettings(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (settings.size() == 0) {
            repository.insertAndUpdate(new Setting(null, new ObjectId(userId), false));
            return Optional.of(new SettingsResponse(false));
        }
        Setting setting = settings.get(0);
        return Optional.of(objectMapper.convertValue(setting, SettingsResponse.class));
    }

    @Override
    public void updateSettings(SettingsRequest settingsRequest, String userId) {
        validate(settingsRequest);
        List<Setting> settings = repository.getSettings(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (settings.size() == 0) {
            repository.insertAndUpdate(new Setting(null, new ObjectId(userId), settingsRequest.isDarkTheme()));
        }
        Setting setting = settings.get(0);
        setting.setDarkTheme(settingsRequest.isDarkTheme());
        repository.insertAndUpdate(setting);
    }

    @Override
    public void updatePassword(ChangePasswordRequest changePasswordRequest, String userId) {
        validate(changePasswordRequest);
        PasswordValidator.validatePassword(generateError(ChangePasswordRequest.class),
                changePasswordRequest.getOldPassword(), "oldPassword");
        List<User> users = userRepository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceAccessException("Not found user");
        }
        User user = users.get(0);
        if (bCryptPasswordEncoder().encode(changePasswordRequest.getOldPassword()).compareTo(user.getPassword()) != 0) {
            Map<String, String> error = generateError(ChangePasswordRequest.class);
            error.put("oldPassword", "Old password does not match!");
            throw new InvalidRequestException(error, "Old password not match!");
        }
        ;
        PasswordValidator.validateNewPassword(generateError(ChangePasswordRequest.class),
                changePasswordRequest.getNewPassword(), "newPassword");
        if (changePasswordRequest.getNewPassword().compareTo(changePasswordRequest.getConfirmNewPassword()) != 0) {
            Map<String, String> error = generateError(ChangePasswordRequest.class);
            error.put("newPassword", "Confirm password is different!");
            error.put("confirmPassword", "Confirm password is different!");
            throw new InvalidRequestException(error, "Confirm password is different!");
        }
        user.setPassword(bCryptPasswordEncoder().encode(changePasswordRequest.getNewPassword()));
        userRepository.insertAndUpdate(user);
    }

    @Override
    public void updateAccountInformation(AccountSetting accountSetting, String userId) {
        validate(accountSetting);
        List<User> users = userRepository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceAccessException("Not found user");
        }
        User user = users.get(0);
        user.setUsername(accountSetting.getUsername());
        user.setFirstName(accountSetting.getFirstName());
        user.setLastName(accountSetting.getLastName());
        user.setVerify2FA(accountSetting.isVerify2FA());
        user.setGender(accountSetting.getGender());
        user.setDob(accountSetting.getDob());
        user.setAddress(accountSetting.getAddress());
        userRepository.insertAndUpdate(user);
    }

}
