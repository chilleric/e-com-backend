package com.example.ecom.service.user;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.user.UserRequest;
import com.example.ecom.dto.user.UserResponse;
import com.example.ecom.repository.common_entity.ViewPoint;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

  void createNewUser(UserRequest userRequest, String loginId);

  void updateUserById(String userId, UserRequest userRequest, List<ViewPoint> viewPoints);

  Optional<UserResponse> findOneUserById(String userId);

  Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams,
      String keySort, int page,
      int pageSize, String sortField, String loginId);

  void changeStatusUser(String userId);
}
