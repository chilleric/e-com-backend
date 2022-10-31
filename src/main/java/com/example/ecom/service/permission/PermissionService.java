package com.example.ecom.service.permission;

import java.util.Map;
import java.util.Optional;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.permission.PermissionRequest;
import com.example.ecom.dto.permission.PermissionResponse;

public interface PermissionService {
    Optional<ListWrapperResponse<PermissionResponse>> getPermissions(Map<String, String> allParams, String keySort,
            int page,
            int pageSize, String sortField);

    void addNewPermissions(PermissionRequest permissionRequest);

    void editPermission(PermissionRequest permissionRequest, String id);

    void deletePermission(String id);
}
