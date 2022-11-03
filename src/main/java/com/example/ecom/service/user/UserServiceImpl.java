package com.example.ecom.service.user;

import static java.util.Map.entry;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

@Service
public class UserServiceImpl extends AbstractService<UserRepository> implements UserService {

    @Override
    public void createNewUser(UserRequest userRequest) {
        validate(userRequest);
        List<User> users = repository
                .getUsers(Map.ofEntries(entry("username", userRequest.getUsername())), "", 0, 0, "")
                .get();
        if (users.size() != 0) {
            Map<String, String> error = generateError(UserRequest.class);
            error.put("username", "username existed");
            throw new InvalidRequestException(error, "username existed");
        }
        Date currentTime = DateFormat.getCurrentTime();
        User user = objectMapper.convertValue(userRequest, User.class);
        user.setPassword(bCryptPasswordEncoder().encode(Base64.getEncoder().encode("Abc@1234".getBytes()).toString()));
        user.setTokens(new HashMap<>());
        user.setCreated(currentTime);
        user.setModified(currentTime);
        repository.insertAndUpdate(user);
    }

    public Optional<UserResponse> findOneUserById(String userId, ResponseType type) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user!");
        }
        User user = users.get(0);
        return Optional.of(new UserResponse(user, type));
    }

    @Override
    public void updateUserById(String userId, UserRequest userRequest) {
        validate(userRequest);
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user!");
        }
        User user = users.get(0);
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
            throw new ResourceNotFoundException("Not found user!");
        }
        User user = users.get(0);
        user.setDeleted(user.getDeleted() == 0 ? 1 : 0);

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
