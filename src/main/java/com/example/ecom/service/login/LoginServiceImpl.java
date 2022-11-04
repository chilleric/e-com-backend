package com.example.ecom.service.login;

import static java.util.Map.entry;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class LoginServiceImpl extends AbstractService<UserRepository> implements LoginService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private CodeRepository codeRepository;

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
                throw new ResourceNotFoundException("Not found user with email: " + loginRequest.getUsername());
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
                throw new ResourceNotFoundException(
                        "Not found user with username: " + loginRequest.getUsername());
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
                throw new ResourceNotFoundException(
                        "Not found user with username: " + loginRequest.getUsername());
            }
            user = users.get(0);
        }
        PasswordValidator.validatePassword(generateError(LoginRequest.class), loginRequest.getPassword(), "password");
        if (!user.isVerified())
            throw new InvalidRequestException(generateError(LoginRequest.class), "This account is not verified!");
        if (!bCryptPasswordEncoder().matches(loginRequest.getPassword(),
                user.getPassword())) {
            Map<String, String> error = generateError(LoginRequest.class);
            error.put("password", "password does not match");
            throw new InvalidRequestException(error, "password does not match");
        }
        Date now = new Date();
        if (user.isVerify2FA()) {
            String verify2FACode = UUID.randomUUID().toString();
            emailService
                    .sendSimpleMail(new EmailDetail(user.getEmail(), "Your 2FA code is: " + verify2FACode,
                            "2FA code from forum-api"));
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
            return Optional.of(new LoginResponse("", "", true));
        } else {
            String deviceId = UUID.randomUUID().toString();
            Date expiredDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L);
            Map<String, Date> tokens = user.getTokens();
            tokens.put(deviceId, expiredDate);
            repository.insertAndUpdate(user);
            return Optional.of(new LoginResponse(user.get_id().toString(), deviceId, false));
        }
    }

    @Override
    public void logout(String id, String deviceId) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", id)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user!");
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
            error.put("username", "username existed");
            throw new InvalidRequestException(error, "username existed");
        }
        List<User> usersEmail = repository
                .getUsers(Map.ofEntries(entry("email", registerRequest.getEmail())), "", 0, 0, "").get();
        if (usersEmail.size() != 0) {
            Map<String, String> error = generateError(RegisterRequest.class);
            if (usersEmail.get(0).isVerified()) {
                error.put("email", "This email is taken!");
                throw new InvalidRequestException(error, "This email is taken!");
            } else {
                error.put("email", "Please verify your email!");
                throw new InvalidRequestException(error, "Please verify your email!");

            }
        } else {
            if (repository
                    .getUsers(Map.ofEntries(entry("phone", registerRequest.getPhone())), "", 0, 0, "").get()
                    .size() > 0) {
                Map<String, String> error = generateError(RegisterRequest.class);
                error.put("phone", "Phone number is taken!");
                throw new InvalidRequestException(error, "Phone number is taken!");
            }
            Map<String, String> error = generateError(RegisterRequest.class);
            PasswordValidator.validateNewPassword(error, registerRequest.getPassword(), "password");
            String passwordEncode = bCryptPasswordEncoder().encode(registerRequest.getPassword());
            User user = objectMapper.convertValue(registerRequest, User.class);
            user.setPassword(passwordEncode);
            user.setTokens(new HashMap<>());
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
                    .sendSimpleMail(new EmailDetail(user.getEmail(), newCode, "Sign up code from forum-api"));
        }
    }

    @Override
    public void verifyRegister(String inputCode, String email) {
        User user = new User();
        if (email.matches(TypeValidation.EMAIL)) {
            List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException("Not found user with email: " + email);
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
                    throw new InvalidRequestException(new HashMap<>(), "This code is invalid");
                else if (code.getExpiredDate().compareTo(now) < 0) {
                    throw new InvalidRequestException(new HashMap<>(), "Code is expired");
                }
            } else {
                throw new InvalidRequestException(new HashMap<>(), "This code is invalid");
            }
        } else {
            throw new InvalidRequestException(new HashMap<>(), "This code is invalid");
        }
        user.setVerified(true);
        repository.insertAndUpdate(user);
    }

    @Override
    public void resendVerifyRegister(String email) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user with email: " + email);
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
                        "Sign up code from forum-api"));

    }

    @Override
    public void forgotPassword(String email) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user with email: " + email);
        }
        User userCheckMail = users.get(0);
        String newPassword = UUID.randomUUID().toString();
        userCheckMail
                .setPassword(
                        bCryptPasswordEncoder().encode(Base64.getEncoder().encodeToString(newPassword.getBytes())));
        repository.insertAndUpdate(userCheckMail);
        emailService.sendSimpleMail(new EmailDetail(userCheckMail.getEmail(),
                "Your new username: " + userCheckMail.getUsername() + " \n" + "Your new password: " + newPassword,
                "Your new password!"));
    }

    @Override
    public Optional<LoginResponse> verify2FA(String email, String inputCode) {
        User user = new User();
        if (email.matches(TypeValidation.EMAIL)) {
            List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException("Not found user with email: " + email);
            }
            user = users.get(0);
        } else {
            List<User> users = repository.getUsers(Map.ofEntries(entry("username", email)), "", 0, 0, "").get();
            if (users.size() == 0) {
                throw new ResourceNotFoundException(
                        "Not found user with username: " + email);
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
                    throw new InvalidRequestException(new HashMap<>(), "This code is invalid");
                else if (code.getExpiredDate().compareTo(now) < 0) {
                    throw new InvalidRequestException(new HashMap<>(), "Code is expired");
                }
            } else {
                throw new InvalidRequestException(new HashMap<>(), "This code is invalid");
            }
        } else {
            throw new InvalidRequestException(new HashMap<>(), "This code is invalid");
        }
        String deviceId = UUID.randomUUID().toString();
        return Optional.of(new LoginResponse(user.get_id().toString(), deviceId, false));
    }

    @Override
    public void resend2FACode(String email) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user with email: " + email);
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
                        "Verify 2FA code from forum-api"));
    }

}
