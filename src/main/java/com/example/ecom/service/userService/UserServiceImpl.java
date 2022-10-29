package com.example.ecom.service.userService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Map.entry;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.userDTO.UserRequest;
import com.example.ecom.dto.userDTO.UserResponse;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.repository.userRepository.User;
import com.example.ecom.repository.userRepository.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.DateFormat;
import com.example.ecom.utils.UserUtils;

@Service
public class UserServiceImpl extends AbstractService<UserRepository> implements UserService {

    @Autowired
    private UserUtils userUtils;

    @Override
    public void createNewUser(UserRequest userRequest) {
        List<User> users = repository
                .getUsers(Map.ofEntries(entry("username", userRequest.getUsername())), "", 0, 0, "")
                .get();
        if (users.size() != 0) {
            throw new InvalidRequestException("username existed");
        }
        // validate(userRequest);
        String passwordEncode = bCryptPasswordEncoder().encode(userRequest.getPassword());
        Date currentTime = DateFormat.getCurrentTime();
        userRequest.setPassword(passwordEncode);
        User user = objectMapper.convertValue(userRequest, User.class);
        user.setTokens(new HashMap<>());
        user.setCreated(currentTime);
        user.setModified(currentTime);
        repository.insertAndUpdate(user);
    }

    public Optional<UserResponse> findOneUserById(String userId) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user!");
        }
        User user = users.get(0);
        return Optional.of(userUtils.generateUserResponse(user, ""));
    }

    @Override
    public void updateUserById(String userId, UserRequest userRequest) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user!");
        }
        User user = users.get(0);

        Date currentTime = DateFormat.getCurrentTime();
        User newUser = objectMapper.convertValue(userRequest, User.class);
        newUser.setTokens(user.getTokens());
        newUser.setCreated(user.getCreated());
        newUser.setModified(currentTime);
        newUser.setVerified(user.isVerified());
        newUser.setVerify2FA(user.isVerify2FA());
        newUser.set_id(user.get_id());

        repository.insertAndUpdate(newUser);
    }

    @Override
    public void deleteUserById(String userId) {
        List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
        if (users.size() == 0) {
            throw new ResourceNotFoundException("Not found user!");
        }
        User user = users.get(0);
        user.setDeleted(1);

        repository.insertAndUpdate(user);
    }

    @Override
    public Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams, String keySort, int page,
            int pageSize, String sortField) {
        List<User> users = repository.getUsers(new HashMap<>(), "", page, pageSize, sortField).get();

        return Optional.of(new ListWrapperResponse<UserResponse>(
                users.stream().map(user -> userUtils.generateUserResponse(user, "")).collect(Collectors.toList()), page,
                pageSize,
                repository.getTotalPage(allParams)));
    }
}
