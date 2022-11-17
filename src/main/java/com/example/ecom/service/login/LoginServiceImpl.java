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
import com.example.ecom.repository.code.Code;
import com.example.ecom.repository.code.CodeRepository;
import com.example.ecom.repository.code.TypeCode;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.PasswordValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Map.entry;

@Service
public class LoginServiceImpl extends AbstractService<UserRepository> implements LoginService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private CodeRepository codeRepository;

    @Value("${default.password}")
    protected String defaultPassword;

    @Override
    public Optional<LoginResponse> login(LoginRequest loginRequest, boolean isRegister) {
        validate(loginRequest);
        User user = new User();
        boolean normalUsername = true;
        if (loginRequest.getUsername().matches(TypeValidation.EMAIL)) {
            List<User> users = repository
                    .getUsers(Map.ofEntries(entry("email", loginRequest.getUsername()), entry("deleted", "0")), "", 0,
                            0, "")
                    .get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL);
            }
            normalUsername = false;
            user = users.get(0);
        }
        if (loginRequest.getUsername().matches(TypeValidation.PHONE)) {
            List<User> users = repository
                    .getUsers(Map.ofEntries(entry("phone", loginRequest.getUsername()), entry("deleted", "0")), "", 0,
                            0, "")
                    .get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_PHONE_NUMBER);
            }
            normalUsername = false;
            user = users.get(0);
        }
        if (normalUsername) {
            List<User> users = repository
                    .getUsers(Map.ofEntries(entry("username", loginRequest.getUsername()), entry("deleted", "0")), "",
                            0, 0, "")
                    .get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USERNAME);
            }
            user = users.get(0);
        }
        PasswordValidator.validatePassword(generateError(LoginRequest.class), loginRequest.getPassword(), "password");
        if (!user.isVerified())
            return Optional.of(new LoginResponse("", "", false, true));
        if (!bCryptPasswordEncoder().matches(loginRequest.getPassword(),
                user.getPassword())) {
            Map<String, String> error = generateError(LoginRequest.class);
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
            Optional<List<Code>> codes = codeRepository.getCodesByType(user.get_id().toString(),
                    TypeCode.VERIFY2FA.name());
            if (codes.isPresent()) {
                Code code = codes.get().get(0);
                code.setCode(verify2FACode);
                code.setExpiredDate(null);
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
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", id)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER);
        }
        User user = users.get(0);
        if (user.getTokens() != null) {
            user.getTokens().remove(deviceId);
        }
        repository.insertAndUpdate(user);
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        validate(registerRequest);
        List<User> users = repository
                .getUsers(Map.ofEntries(entry("username", registerRequest.getUsername())), "", 0, 0, "").get();
        if (users.size() != 0) {
            Map<String, String> error = generateError(RegisterRequest.class);
            error.put("username", LanguageMessageKey.USERNAME_EXISTED);
            throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
        }
        List<User> usersEmail = repository
                .getUsers(Map.ofEntries(entry("email", registerRequest.getEmail())), "", 0, 0, "").get();
        if (usersEmail.size() != 0) {
            Map<String, String> error = generateError(RegisterRequest.class);
            if (usersEmail.get(0).isVerified()) {
                error.put("email", LanguageMessageKey.EMAIL_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.EMAIL_TAKEN);
            } else {
                error.put("email", LanguageMessageKey.PLEASE_VERIFY_EMAIL);
                throw new InvalidRequestException(error, LanguageMessageKey.PLEASE_VERIFY_EMAIL);
            }
        } else {
            if (repository
                    .getUsers(Map.ofEntries(entry("phone", registerRequest.getPhone())), "", 0, 0, "").get()
                    .size() > 0) {
                Map<String, String> error = generateError(RegisterRequest.class);
                error.put("phone", LanguageMessageKey.PHONE_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.PHONE_TAKEN);
            }
            Map<String, String> error = generateError(RegisterRequest.class);
            PasswordValidator.validateNewPassword(error, registerRequest.getPassword(), "password");
            String passwordEncode = bCryptPasswordEncoder().encode(registerRequest.getPassword());
            User user = objectMapper.convertValue(registerRequest, User.class);
            user.setPassword(passwordEncode);
            user.setTokens(new HashMap<>());
            user.setGender(0);
            user.setDob("");
            repository.insertAndUpdate(user);
            String newCode = RandomStringUtils.randomAlphabetic(6);
            Date now = new Date();
            Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
            Optional<List<Code>> codes = codeRepository.getCodesByType(user.get_id().toString(),
                    TypeCode.REGISTER.name());
            if (codes.isPresent()) {
                if (codes.get().size() > 0) {
                    Code code = codes.get().get(0);
                    code.setCode(newCode);
                    code.setExpiredDate(expiredDate);
                    codeRepository.insertAndUpdateCode(code);
                } else {
                    Code code = new Code(null, user.get_id(), TypeCode.REGISTER, newCode, expiredDate);
                    codeRepository.insertAndUpdateCode(code);
                }
            } else {
                Code code = new Code(null, user.get_id(), TypeCode.REGISTER, newCode, expiredDate);
                codeRepository.insertAndUpdateCode(code);
            }
            emailService
                    .sendSimpleMail(new EmailDetail(user.getEmail(), newCode, "OTP"));
        }
    }

    @Override
    public void verifyRegister(String inputCode, String email) {
        User user = new User();
        if (email.matches(TypeValidation.EMAIL)) {
            List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL);
            } else {
                user = users.get(0);
            }
        }
        Date now = new Date();
        Optional<List<Code>> codes = codeRepository.getCodesByType(user.get_id().toString(),
                TypeCode.REGISTER.name());
        if (codes.isPresent()) {
            if (codes.get().size() > 0) {
                Code code = codes.get().get(0);
                if (code.getCode().compareTo(inputCode) != 0)
                    throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
                else if (code.getExpiredDate().compareTo(now) < 0) {
                    throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.CODE_EXPIRED);
                }
            } else {
                throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
            }
        } else {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
        }
        user.setVerified(true);
        repository.insertAndUpdate(user);
    }

    @Override
    public void resendVerifyRegister(String email) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL);
        }
        User userCheckMail = users.get(0);
        String newCode = RandomStringUtils.randomAlphabetic(6);
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
        Optional<List<Code>> codes = codeRepository.getCodesByType(userCheckMail.get_id().toString(),
                TypeCode.REGISTER.name());
        if (codes.isPresent()) {
            if (codes.get().size() > 0) {
                Code code = codes.get().get(0);
                code.setCode(newCode);
                code.setExpiredDate(expiredDate);
                codeRepository.insertAndUpdateCode(code);
            } else {
                Code code = new Code(null, userCheckMail.get_id(), TypeCode.REGISTER, newCode, expiredDate);
                codeRepository.insertAndUpdateCode(code);
            }
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
        List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL);
        }
        User userCheckMail = users.get(0);
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
        if (email.matches(TypeValidation.EMAIL)) {
            List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL);
            }
            user = users.get(0);
        } else {
            List<User> users = repository.getUsers(Map.ofEntries(entry("username", email)), "", 0, 0, "").get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER);
            }
            user = users.get(0);
        }
        Date now = new Date();
        Optional<List<Code>> codes = codeRepository.getCodesByType(user.get_id().toString(),
                TypeCode.VERIFY2FA.name());
        if (codes.isPresent()) {
            if (codes.get().size() > 0) {
                Code code = codes.get().get(0);
                if (code.getCode().compareTo(inputCode) != 0)
                    throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
                else if (code.getExpiredDate().compareTo(now) < 0) {
                    throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.CODE_EXPIRED);
                }
            } else {
                throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
            }
        } else {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.INVALID_CODE);
        }
        String deviceId = UUID.randomUUID().toString();
        return Optional.of(new LoginResponse(user.get_id().toString(), deviceId, false, false));
    }

    @Override
    public void resend2FACode(String email) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_EMAIL);
        }
        User userCheckMail = users.get(0);
        String newCode = RandomStringUtils.randomAlphabetic(6);
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + 5 * 60 * 1000L);
        Optional<List<Code>> codes = codeRepository.getCodesByType(userCheckMail.get_id().toString(),
                TypeCode.VERIFY2FA.name());
        if (codes.isPresent()) {
            if (codes.get().size() > 0) {
                Code code = codes.get().get(0);
                code.setCode(newCode);
                code.setExpiredDate(expiredDate);
                codeRepository.insertAndUpdateCode(code);
            } else {
                Code code = new Code(null, userCheckMail.get_id(), TypeCode.VERIFY2FA, newCode, expiredDate);
                codeRepository.insertAndUpdateCode(code);
            }
        } else {
            Code code = new Code(null, userCheckMail.get_id(), TypeCode.VERIFY2FA, newCode, expiredDate);
            codeRepository.insertAndUpdateCode(code);
        }
        emailService
                .sendSimpleMail(new EmailDetail(userCheckMail.getEmail(), newCode,
                        "OTP"));
    }

}
