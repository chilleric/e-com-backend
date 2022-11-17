package com.example.ecom.service.user;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.constant.ResponseType;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.user.UserRequest;
import com.example.ecom.dto.user.UserResponse;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.DateFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Service
public class UserServiceImpl extends AbstractService<UserRepository> implements UserService {

    @Value("${default.password}")
    protected String defaultPassword;

    @Override
    public void createNewUser(UserRequest userRequest) {
        validate(userRequest);
        List<User> users = repository
                .getUsers(Map.ofEntries(entry("username", userRequest.getUsername())), "", 0, 0, "")
                .get();
        if (users.size() != 0) {
            Map<String, String> error = generateError(UserRequest.class);
            error.put("username", LanguageMessageKey.USERNAME_EXISTED);
            throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
        }
        Date currentTime = DateFormat.getCurrentTime();
        User user = objectMapper.convertValue(userRequest, User.class);
        user.setPassword(
                bCryptPasswordEncoder().encode(Base64.getEncoder().encodeToString(defaultPassword.getBytes())));
        user.setTokens(new HashMap<>());
        user.setCreated(currentTime);
        user.setModified(currentTime);
        repository.insertAndUpdate(user);
    }

    public Optional<UserResponse> findOneUserById(String userId, ResponseType type) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER);
        }
        User user = users.get(0);
        return Optional.of(new UserResponse(user, type));
    }

    @Override
    public void updateUserById(String userId, UserRequest userRequest) {
        validate(userRequest);
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER);
        }
        List<User> emailCheck = repository
                .getUsers(Map.ofEntries(entry("email", userRequest.getEmail())), userId, 0, 0, userId).get();
        List<User> phoneCheck = repository
                .getUsers(Map.ofEntries(entry("phone", userRequest.getPhone())), userId, 0, 0, userId).get();
        Map<String, String> error = generateError(UserRequest.class);
        if (emailCheck.size() > 0) {
            if (emailCheck.get(0).get_id().compareTo(users.get(0).get_id()) != 0) {
                error.put("email", LanguageMessageKey.EMAIL_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.EMAIL_TAKEN);

            }
        }
        if (phoneCheck.size() > 0) {
            if (phoneCheck.get(0).get_id().compareTo(users.get(0).get_id()) != 0) {
                error.put("phone", LanguageMessageKey.PHONE_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.PHONE_TAKEN);
            }
        }
        List<User> usernameCheck = repository
                .getUsers(Map.ofEntries(entry("username", userRequest.getUsername())), "", 0, 0, "")
                .get();
        if (usernameCheck.size() != 0) {
            if (usernameCheck.get(0).get_id().compareTo(users.get(0).get_id()) != 0) {
                error.put("username", LanguageMessageKey.USERNAME_EXISTED);
                throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
            }
        }
        User user = users.get(0);
        if (user.getUsername().compareTo("super_admin") == 0) {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.FORBIDDEN);
        }
        Date currentTime = DateFormat.getCurrentTime();
        User newUser = objectMapper.convertValue(userRequest, User.class);
        newUser.setPassword(user.getPassword());
        newUser.setTokens(user.getTokens());
        newUser.setCreated(user.getCreated());
        newUser.setModified(currentTime);
        newUser.setVerified(user.isVerified());
        newUser.setVerify2FA(user.isVerify2FA());
        newUser.set_id(user.get_id());

        repository.insertAndUpdate(newUser);
    }

    @Override
    public void changeStatusUser(String userId) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER);
        }
        User user = users.get(0);
        if (user.getUsername().compareTo("super_admin") == 0) {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.FORBIDDEN);
        }
        user.setDeleted(user.getDeleted() == 0 ? 1 : 0);
        user.setModified(DateFormat.getCurrentTime());

        repository.insertAndUpdate(user);
    }

    @Override
    public Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams, String keySort, int page,
                                                                int pageSize, String sortField, ResponseType type) {
        if (type.compareTo(ResponseType.PUBLIC) == 0) {
            allParams.put("deleted", "0");
        }
        List<User> users = repository.getUsers(allParams, "", page, pageSize, sortField).get();
        return Optional.of(new ListWrapperResponse<UserResponse>(
                users.stream().map(user -> new UserResponse(user, type)).collect(Collectors.toList()),
                page,
                pageSize,
                repository.getTotalPage(allParams)));
    }
}
