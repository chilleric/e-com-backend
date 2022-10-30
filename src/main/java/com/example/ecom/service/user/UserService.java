package com.example.ecom.service.user;

import java.util.Map;
import java.util.Optional;

import com.example.ecom.constant.ResponseType;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.user.UserRequest;
import com.example.ecom.dto.user.UserResponse;

public interface UserService {
    void createNewUser(UserRequest userRequest);

    void updateUserById(String userId, UserRequest userRequest);

    Optional<UserResponse> findOneUserById(String userId, ResponseType type);

    Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams, String keySort, int page,
            int pageSize, String sortField, ResponseType type);

    void deleteUserById(String userId);
}
