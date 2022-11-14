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
import com.example.ecom.repository.language.Language;
import com.example.ecom.repository.language.LanguageRepository;
import com.example.ecom.repository.settings.Setting;
import com.example.ecom.repository.settings.SettingRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.DateFormat;
import com.example.ecom.utils.PasswordValidator;

@Service
public class SettingServiceImpl extends AbstractService<SettingRepository> implements SettingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Override
    public Optional<SettingsResponse> getSettingsByUserId(String userId) {
        List<Setting> settings = repository.getSettings(Map.ofEntries(entry("userId", userId)), "", 0, 0, "").get();

        if (settings.size() == 0) {
            repository.insertAndUpdate(new Setting(null, new ObjectId(userId), false, "en"));
            return Optional.of(new SettingsResponse(false, "en"));
        }
        Setting setting = settings.get(0);
        return Optional.of(objectMapper.convertValue(setting, SettingsResponse.class));
    }

    @Override
    public void updateSettings(SettingsRequest settingsRequest, String userId) {
        validate(settingsRequest);
        List<Language> languages = languageRepository
                .getLanguages(Map.ofEntries(
                        entry("key", settingsRequest.getLanguageKey().toLowerCase())), "", 0,
                        0,
                        "")
                .get();
        if (languages.size() == 0) {
            Map<String, String> error = generateError(SettingsRequest.class);
            error.put("languageKey", "Language is invalid");
            throw new InvalidRequestException(error, "Language is invalid");
        }
        List<Setting> settings = repository.getSettings(Map.ofEntries(entry("userId", userId)), "", 0, 0, "").get();
        if (settings.size() == 0) {
            repository.insertAndUpdate(new Setting(null, new ObjectId(userId), settingsRequest.isDarkTheme(), "en"));
        } else {
            Setting setting = settings.get(0);
            setting.setDarkTheme(settingsRequest.isDarkTheme());
            setting.setLanguageKey(settingsRequest.getLanguageKey());
            repository.insertAndUpdate(setting);
        }

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
        if (!bCryptPasswordEncoder().matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
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
        user.setModified(DateFormat.getCurrentTime());
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
        List<User> emailCheck = userRepository
                .getUsers(Map.ofEntries(entry("email", accountSetting.getEmail())), userId, 0, 0, userId).get();
        List<User> phoneCheck = userRepository
                .getUsers(Map.ofEntries(entry("phone", accountSetting.getPhone())), userId, 0, 0, userId).get();
        Map<String, String> error = generateError(AccountSetting.class);
        if (emailCheck.size() > 0) {
            if (emailCheck.get(0).get_id().compareTo((users.get(0).get_id())) != 0) {
                error.put("email", "This email is taken!");
                throw new InvalidRequestException(error, "Phone or email is taken!");
            }
        }
        if (phoneCheck.size() > 0) {
            if (phoneCheck.get(0).get_id().compareTo((users.get(0).get_id())) != 0) {
                error.put("phone", "This phone is taken!");
                throw new InvalidRequestException(error, "Phone or email is taken!");
            }
        }
        User user = users.get(0);
        user.setPhone(accountSetting.getPhone());
        user.setEmail(accountSetting.getEmail());
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
