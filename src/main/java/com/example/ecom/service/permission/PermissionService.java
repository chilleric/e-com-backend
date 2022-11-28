package com.example.ecom.service.permission;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.permission.PermissionRequest;
import com.example.ecom.dto.permission.PermissionResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PermissionService {
  Optional<ListWrapperResponse<PermissionResponse>> getPermissions(Map<String, String> allParams,
      String keySort,
      int page,
      int pageSize, String sortField, boolean skipAccessability, String loginId);

  void addNewPermissions(PermissionRequest permissionRequest, String loginId);

  void editPermission(PermissionRequest permissionRequest, String id);

  void deletePermission(String id);
  Map<String, List<String>> getViewPointSelect();

}
