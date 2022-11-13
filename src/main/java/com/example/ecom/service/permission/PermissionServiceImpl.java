package com.example.ecom.service.permission;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecom.constant.DateTime;
import com.example.ecom.constant.ResponseType;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.feature.FeatureResponse;
import com.example.ecom.dto.permission.PermissionRequest;
import com.example.ecom.dto.permission.PermissionResponse;
import com.example.ecom.dto.user.UserResponse;
import com.example.ecom.exception.ForbiddenException;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.service.feature.FeatureService;
import com.example.ecom.service.user.UserService;
import com.example.ecom.utils.DateFormat;

@Service
public class PermissionServiceImpl extends AbstractService<PermissionRepository> implements PermissionService {
    @Autowired
    private FeatureService featureService;

    @Autowired
    private UserService userService;

    @Override
    public Optional<ListWrapperResponse<PermissionResponse>> getPermissions(Map<String, String> allParams,
            String keySort, int page, int pageSize, String sortField) {
        List<Permission> permissions = repository.getPermissions(allParams, keySort, page, pageSize, sortField).get();
        return Optional.of(new ListWrapperResponse<PermissionResponse>(
                permissions.stream()
                        .map(permission -> new PermissionResponse(permission.get_id().toString(), permission.getName(),
                                permission.getFeatureId().size() > 0
                                        ? permission.getFeatureId().stream()
                                                .map(feature -> feature.toString()).collect(Collectors.toList())
                                        : new ArrayList<>(),
                                permission.getUserId().size() > 0
                                        ? permission.getUserId().stream().map(userId -> userId.toString())
                                                .collect(Collectors.toList())
                                        : new ArrayList<>(),
                                DateFormat.toDateString(permission.getCreated(), DateTime.YYYY_MM_DD),
                                DateFormat.toDateString(permission.getModified(), DateTime.YYYY_MM_DD),
                                permission.getSkipAccessability()))
                        .collect(Collectors.toList()),
                page, pageSize, permissions.size()));
    }

    @Override
    public void addNewPermissions(PermissionRequest permissionRequest) {
        validate(permissionRequest);
        List<Permission> permissions = repository
                .getPermissions(Map.ofEntries(entry("name", permissionRequest.getName())), "", 0, 0, "").get();
        if (permissions.size() != 0) {
            Map<String, String> error = generateError(PermissionRequest.class);
            error.put("name", "This name is unavailable!");
            throw new InvalidRequestException(error, "This name is unavailable!");
        }
        Permission permission = new Permission();
        permission.setCanDelete(true);
        permission.setName(permissionRequest.getName());
        permission.setCreated(DateFormat.getCurrentTime());
        permission.setSkipAccessability(1);
        if (permissionRequest.getFeatureId().size() != 0) {
            List<FeatureResponse> featureResponse = generateFeatureList(permissionRequest.getFeatureId());
            permission.setFeatureId(
                    featureResponse.stream().map(feature -> new ObjectId(feature.getId()))
                            .collect(Collectors.toList()));
        } else {
            permission.setFeatureId(new ArrayList<>());
        }
        if (permissionRequest.getUserId().size() != 0) {
            List<UserResponse> userResponse = generateUserList(permissionRequest.getUserId());
            permission
                    .setUserId(
                            userResponse.stream().map(user -> new ObjectId(user.getId())).collect(Collectors.toList()));
        } else {
            permission.setUserId(new ArrayList<>());
        }
        repository.insertAndUpdate(permission);
    }

    @Override
    public void editPermission(PermissionRequest permissionRequest, String id) {
        Permission permission = repository.getPermissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found permission!"));
        validate(permissionRequest);
        List<Permission> permissions = repository
                .getPermissions(Map.ofEntries(entry("name", permissionRequest.getName())), "", 0, 0, "").get();
        if (permissions.size() != 0) {
            if (permissions.get(0).get_id().compareTo(permission.get_id()) != 0) {
                Map<String, String> error = generateError(PermissionRequest.class);
                error.put("name", "This name is unavailable!");
                throw new InvalidRequestException(error, "This name is unavailable!");
            }
        }
        if (permission.getName().compareTo("super_admin_permission") == 0) {
            UserResponse adminUser = userService
                    .getUsers(Map.ofEntries(entry("username", "super_admin")), "", 0, 0, "", ResponseType.PRIVATE)
                    .get()
                    .getData().get(0);
            if (!permissionRequest.getUserId().contains(adminUser.getId())) {
                Map<String, String> error = generateError(PermissionRequest.class);
                error.put("userId", "Must contain admin account!");
                throw new InvalidRequestException(error, "Must contain admin account!");
            }
            permission.setUserId(permissionRequest.getUserId().stream().map(userId -> new ObjectId(userId))
                    .collect(Collectors.toList()));
            permission.setModified(DateFormat.getCurrentTime());
            repository.insertAndUpdate(permission);
        } else {
            permission.setName(permissionRequest.getName());
            if (permissionRequest.getFeatureId().size() != 0) {
                List<FeatureResponse> featureResponse = generateFeatureList(permissionRequest.getFeatureId());
                permission.setFeatureId(
                        featureResponse.stream().map(feature -> new ObjectId(feature.getId()))
                                .collect(Collectors.toList()));
            } else {
                permission.setFeatureId(new ArrayList<>());
            }
            if (permissionRequest.getUserId().size() != 0) {
                List<UserResponse> userResponse = generateUserList(permissionRequest.getUserId());
                permission
                        .setUserId(
                                userResponse.stream().map(user -> new ObjectId(user.getId()))
                                        .collect(Collectors.toList()));
            } else {
                permission.setUserId(new ArrayList<>());
            }
            permission.setSkipAccessability(permissionRequest.getSkipAccessability());
            permission.setModified(DateFormat.getCurrentTime());
            repository.insertAndUpdate(permission);
        }

    }

    @Override
    public void deletePermission(String id) {
        Permission permission = repository.getPermissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found permission!"));
        if (permission.isCanDelete()) {
            repository.deletePermission(id);
        } else {
            throw new ForbiddenException("Access denied!");
        }
    }

    private String generateParamsValue(List<String> features) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < features.size(); i++) {
            result.append(features.get(i));
            if (i != features.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    private List<FeatureResponse> generateFeatureList(List<String> features) {
        String result = generateParamsValue(features);
        return featureService.getFeatures(Map.ofEntries(entry("_id", result.toString())), "", 0, 0, "").get().getData();
    }

    private List<UserResponse> generateUserList(List<String> users) {
        String result = generateParamsValue(users);
        return userService.getUsers(Map.ofEntries(entry("_id", result.toString())), "", 0, 0, "", ResponseType.PRIVATE)
                .get()
                .getData();
    }
}
