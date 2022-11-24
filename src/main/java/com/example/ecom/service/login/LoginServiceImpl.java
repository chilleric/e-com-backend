package com.example.ecom.service.login;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.constant.TypeValidation;
import com.example.ecom.dto.login.LoginRequest;
import com.example.ecom.dto.login.LoginResponse;
import com.example.ecom.dto.login.RegisterRequest;
import com.example.ecom.email.EmailDetail;
import com.example.ecom.email.EmailService;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.inventory.permission.PermissionInventory;
import com.example.ecom.inventory.user.UserInventory;
import com.example.ecom.repository.code.Code;
import com.example.ecom.repository.code.CodeRepository;
import com.example.ecom.repository.code.TypeCode;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.DateFormat;
import com.example.ecom.utils.PasswordValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoginServiceImpl extends AbstractService<UserRepository> implements LoginService {

    @Value("${default.password}")
    protected String defaultPassword;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CodeRepository codeRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserInventory userInventory;

    @Autowired
    private PermissionInventory permissionInventory;

    @Override
    public Optional<LoginResponse> login(LoginRequest loginRequest, boolean isRegister) {
        validate(loginRequest);
        User user = new User();
        boolean normalUsername = true;
        if (loginRequest.getUsername().matches(TypeValidation.EMAIL)) {
            user = userInventory.findUserByEmail(loginRequest.getUsername()).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
            normalUsername = false;
        }
        if (loginRequest.getUsername().matches(TypeValidation.PHONE)) {
            user = userInventory.findUserByPhone(loginRequest.getUsername()).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_PHONE_NUMBER));
            normalUsername = false;
        }
        if (normalUsername) {
            user = userInventory.findUserByUsername(loginRequest.getUsername()).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USERNAME));
        }
        Map<String, String> error = generateError(LoginRequest.class);
        PasswordValidator.validatePassword(generateError(LoginRequest.class), loginRequest.getPassword(), "password");
        if (!user.isVerified())
            return Optional.of(new LoginResponse("", "", false, true));
        if (!bCryptPasswordEncoder().matches(loginRequest.getPassword(),
                user.getPassword())) {
            error.put("password", LanguageMessageKey.PASSWORD_NOT_MATCH);
            throw new InvalidRequestException(error, LanguageMessageKey.PASSWORD_NOT_MATCH);
        }
        Date now = new Date();
        if (user.isVerify2FA()) {
            String verify2FACode = RandomStringUtils.randomAlphabetic(6);
            emailService
                    .sendSimpleMail(new EmailDetail(user.getEmail(), verify2FACode,
                            "OTP"));
            Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
            Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
                    TypeCode.VERIFY2FA.name());
            if (codes.isPresent()) {
                Code code = codes.get();
                code.setCode(verify2FACode);
                code.setExpiredDate(expiredDate);
                codeRepository.insertAndUpdateCode(code);
            } else {
                Code code = new Code(null, user.get_id(), TypeCode.VERIFY2FA, verify2FACode, expiredDate);
                codeRepository.insertAndUpdateCode(code);
            }
            return Optional.of(new LoginResponse("", "", true, false));
        } else {
            String deviceId = UUID.randomUUID().toString();
            Date expiredDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L);
            Map<String, Date> tokens = user.getTokens();
            tokens.put(deviceId, expiredDate);
            repository.insertAndUpdate(user);
            return Optional.of(new LoginResponse(user.get_id().toString(), deviceId, false, false));
        }
    }

    @Override
    public void logout(String id, String deviceId) {
        User user = userInventory.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER));
        if (user.getTokens() != null) {
            user.getTokens().remove(deviceId);
        }
        repository.insertAndUpdate(user);
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        validate(registerRequest);
        Map<String, String> error = generateError(RegisterRequest.class);
        userInventory.findUserByUsername(registerRequest.getUsername()).ifPresent(username -> {
            error.put("username", LanguageMessageKey.USERNAME_EXISTED);
            throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
        });
        userInventory.findUserByEmail(registerRequest.getEmail()).ifPresent(userEmail -> {
            if (userEmail.isVerified()) {
                error.put("email", LanguageMessageKey.EMAIL_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.EMAIL_TAKEN);
            } else {
                error.put("email", LanguageMessageKey.PLEASE_VERIFY_EMAIL);
                throw new InvalidRequestException(error, LanguageMessageKey.PLEASE_VERIFY_EMAIL);
            }
        });
        userInventory.findUserByPhone(registerRequest.getPhone()).ifPresent(userPhone -> {
            error.put("phone", LanguageMessageKey.PHONE_TAKEN);
            throw new InvalidRequestException(error, LanguageMessageKey.PHONE_TAKEN);
        });
        PasswordValidator.validateNewPassword(error, registerRequest.getPassword(), "password");
        String passwordEncode = bCryptPasswordEncoder().encode(registerRequest.getPassword());
        User user = objectMapper.convertValue(registerRequest, User.class);
        ObjectId newId = new ObjectId();
        user.set_id(newId);
        user.setPassword(passwordEncode);
        user.setTokens(new HashMap<>());
        user.setGender(0);
        user.setDob("");
        Permission defaultPerm = permissionInventory.getPermissionByName("default_permission")
                .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.PERMISSION_NOT_FOUND));
        List<ObjectId> userIds = defaultPerm.getUserId();
        userIds.add(newId);
        defaultPerm.setUserId(userIds);
        permissionRepository.insertAndUpdate(defaultPerm);
        repository.insertAndUpdate(user);
        String newCode = RandomStringUtils.randomAlphabetic(6);
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
        Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
                TypeCode.REGISTER.name());
        if (codes.isPresent()) {
            Code code = codes.get();
            code.setCode(newCode);
            code.setExpiredDate(expiredDate);
            codeRepository.insertAndUpdateCode(code);
        } else {
            Code code = new Code(null, user.get_id(), TypeCode.REGISTER, newCode, expiredDate);
            codeRepository.insertAndUpdateCode(code);
        }
        emailService
                .sendSimpleMail(new EmailDetail(user.getEmail(), newCode, "OTP"));

    }

    @Override
    public void verifyRegister(String inputCode, String email) {
        User user = new User();
        if (email.matches(TypeValidation.EMAIL)) {
            user = userInventory.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
        }
        Date now = new Date();
        Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
                TypeCode.REGISTER.name());
        if (codes.isPresent()) {
            Code code = codes.get();
            if (code.getCode().compareTo(inputCode) != 0)
                throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
            else if (code.getExpiredDate().compareTo(now) < 0) {
                throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.CODE_EXPIRED);
            }
        } else {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
        }
        user.setVerified(true);
        repository.insertAndUpdate(user);
    }

    @Override
    public void resendVerifyRegister(String email) {
        User userCheckMail = userInventory.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
        String newCode = RandomStringUtils.randomAlphabetic(6);
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
        Optional<Code> codes = codeRepository.getCodesByType(userCheckMail.get_id().toString(),
                TypeCode.REGISTER.name());
        if (codes.isPresent()) {
            Code code = codes.get();
            code.setCode(newCode);
            code.setExpiredDate(expiredDate);
            codeRepository.insertAndUpdateCode(code);
        } else {
            Code code = new Code(null, userCheckMail.get_id(), TypeCode.REGISTER, newCode, expiredDate);
            codeRepository.insertAndUpdateCode(code);
        }
        emailService
                .sendSimpleMail(new EmailDetail(userCheckMail.getEmail(), newCode,
                        "OTP"));

    }

    @Override
    public void forgotPassword(String email) {
        User userCheckMail = userInventory.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
        userCheckMail
                .setPassword(
                        bCryptPasswordEncoder().encode(Base64.getEncoder().encodeToString(defaultPassword.getBytes())));
        repository.insertAndUpdate(userCheckMail);
        emailService.sendSimpleMail(new EmailDetail(userCheckMail.getEmail(),
                "Username: " + userCheckMail.getUsername() + " \n" + "Password: " + defaultPassword,
                "New password!"));
    }

    @Override
    public Optional<LoginResponse> verify2FA(String email, String inputCode) {
        User user = new User();
        if(email.matches(TypeValidation.EMAIL)){
            user = userInventory.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
        } else if(email.matches(TypeValidation.PHONE)){
            user = userInventory.findUserByPhone(email).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
        } else {
            user = userInventory.findUserByUsername(email).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
        }
        Date now = new Date();
        Optional<Code> codes = codeRepository.getCodesByType(user.get_id().toString(),
                TypeCode.VERIFY2FA.name());
        if (codes.isPresent()) {
            Code code = codes.get();
            if (code.getCode().compareTo(inputCode) != 0)
                throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
            else if (code.getExpiredDate().compareTo(now) < 0) {
                throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.CODE_EXPIRED);
            }
        } else {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
        }
        String deviceId = UUID.randomUUID().toString();
        Map<String, Date> devices = user.getTokens();
        devices.put(deviceId, DateFormat.getCurrentTime());
        repository.insertAndUpdate(user);
        return Optional.of(new LoginResponse(user.get_id().toString(), deviceId, false, false));
    }

    @Override
    public void resend2FACode(String email) {
        User userCheckMail = userInventory.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL));
        String newCode = RandomStringUtils.randomAlphabetic(6);
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
        Optional<Code> codes = codeRepository.getCodesByType(userCheckMail.get_id().toString(),
                TypeCode.VERIFY2FA.name());
        if (codes.isPresent()) {
            Code code = codes.get();
            code.setCode(newCode);
            code.setExpiredDate(expiredDate);
            codeRepository.insertAndUpdateCode(code);
        } else {
            Code code = new Code(null, userCheckMail.get_id(), TypeCode.VERIFY2FA, newCode, expiredDate);
            codeRepository.insertAndUpdateCode(code);
        }
        emailService
                .sendSimpleMail(new EmailDetail(userCheckMail.getEmail(), newCode,
                        "OTP"));
    }

}
