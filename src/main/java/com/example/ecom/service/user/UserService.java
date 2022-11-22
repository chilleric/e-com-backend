package com.example.ecom.service.user;

import com.example.ecom.constant.ResponseType;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.user.UserRequest;
import com.example.ecom.dto.user.UserResponse;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    void createNewUser(UserRequest userRequest, String loginId);

    void updateUserById(String userId, UserRequest userRequest);

    Optional<UserResponse> findOneUserById(String userId, ResponseType type);

    Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams, String keySort, int page,
                                                         int pageSize, String sortField, ResponseType type, boolean skipAccessAbility, String loginId);

    void changeStatusUser(String userId);
}
