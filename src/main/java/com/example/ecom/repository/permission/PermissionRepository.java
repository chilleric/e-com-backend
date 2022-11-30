package com.example.ecom.repository.permission;

import com.example.ecom.repository.common_entity.ViewPoint;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PermissionRepository {

  Optional<List<Permission>> getPermissions(Map<String, String> allParams, String keySort, int page,
      int pageSize,
      String sortField);

  Optional<Permission> getPermissionById(String id);


  Optional<List<Permission>> getPermissionByUserId(String userId);

  void insertAndUpdate(Permission permission);

  void deletePermission(String id);

  Map<String, List<ViewPoint>> getViewPointSelect();

  long getTotal(Map<String, String> allParams);
}
