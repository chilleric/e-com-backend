package com.example.ecom.service.userService;

import java.util.Map;
import java.util.Optional;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.userDTO.UserRequest;
import com.example.ecom.dto.userDTO.UserResponse;

public interface UserService {
    void createNewUser(UserRequest userRequest);

    void updateUserById(String userId, UserRequest userRequest);

    Optional<UserResponse> findOneUserById(String userId);

    Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams, String keySort, int page,
            int pageSize,
            String sortField);

    void deleteUserById(String userId);
}
