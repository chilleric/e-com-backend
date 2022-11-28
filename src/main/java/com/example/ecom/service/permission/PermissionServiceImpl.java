package com.example.ecom.service.permission;

import static java.util.Map.entry;

import com.example.ecom.constant.DateTime;
import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.permission.PermissionRequest;
import com.example.ecom.dto.permission.PermissionResponse;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.exception.ForbiddenException;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.inventory.permission.PermissionInventory;
import com.example.ecom.inventory.user.UserInventory;
import com.example.ecom.repository.accessability.Accessability;
import com.example.ecom.repository.accessability.AccessabilityRepository;
import com.example.ecom.repository.feature.Feature;
import com.example.ecom.repository.feature.FeatureRepository;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends AbstractService<PermissionRepository> implements
    PermissionService {

  @Autowired
  private FeatureRepository featureRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PermissionInventory permissionInventory;

  @Autowired
  private UserInventory userInventory;

  @Autowired
  private AccessabilityRepository accessabilityRepository;

  @Override
  public Optional<ListWrapperResponse<PermissionResponse>> getPermissions(
      Map<String, String> allParams,
      String keySort, int page, int pageSize,
      String sortField, boolean skipAccessability,
      String loginId) {
    List<String> targets = accessabilityRepository.getListTargetId(loginId)
        .orElseThrow(() -> new BadSqlException(LanguageMessageKey.SERVER_ERROR))
        .stream().map(access -> access.getTargetId().toString()).collect(Collectors.toList());
    if (!skipAccessability && allParams.containsKey("_id")) {
      String[] idList = allParams.get("_id").split(",");
      for (int i = 0; i < idList.length; i++) {
        if (!targets.contains(idList[i])) {
          throw new ForbiddenException(LanguageMessageKey.FORBIDDEN);
        }
      }
    }
    if (!skipAccessability && !allParams.containsKey("_id")) {
      allParams.put("_id", generateParamsValue(targets));
      System.out.println(generateParamsValue(targets));
      if (targets.size() == 0) {
        return Optional.of(
            new ListWrapperResponse<PermissionResponse>(new ArrayList<>(), page, pageSize, 0));
      }
    }
    List<Permission> permissions = repository.getPermissions(allParams, keySort, page, pageSize,
        sortField).get();
    return Optional.of(new ListWrapperResponse<PermissionResponse>(
        permissions.stream()
            .map(permission -> new PermissionResponse(permission.get_id().toString(),
                permission.getName(),
                permission.getFeatureId().size() > 0
                    ? permission.getFeatureId().stream()
                    .map(ObjectId::toString).collect(Collectors.toList())
                    : new ArrayList<>(),
                permission.getUserId().size() > 0
                    ? permission.getUserId().stream().map(ObjectId::toString)
                    .collect(Collectors.toList())
                    : new ArrayList<>(),
                DateFormat.toDateString(permission.getCreated(), DateTime.YYYY_MM_DD),
                DateFormat.toDateString(permission.getModified(), DateTime.YYYY_MM_DD),
                permission.getSkipAccessability(), permission.getViewPoints()))
            .collect(Collectors.toList()),
        page, pageSize, repository.getTotal(allParams)));
  }

  @Override
  public void addNewPermissions(PermissionRequest permissionRequest, String loginId) {
    validate(permissionRequest);
    Map<String, String> error = generateError(PermissionRequest.class);
    List<Permission> permissions = repository
        .getPermissions(Map.ofEntries(entry("name", permissionRequest.getName())), "", 0, 0, "")
        .get();
    if (permissions.size() != 0) {
      error.put("name", LanguageMessageKey.INVALID_NAME_PERMISSION);
      throw new InvalidRequestException(error, LanguageMessageKey.INVALID_NAME_PERMISSION);
    }
    permissionRequest.getUserId().forEach(userId -> {
      repository.getPermissionByUserId(userId).ifPresent(thisUser -> {
        error.put("userId", LanguageMessageKey.UNIQUE_USER_PERMISSION);
        throw new InvalidRequestException(error, LanguageMessageKey.UNIQUE_USER_PERMISSION);
      });
    });
    Permission permission = new Permission();
    ObjectId newId = new ObjectId();
    permission.set_id(newId);
    permission.setCanDelete(true);
    permission.setName(permissionRequest.getName());
    permission.setCreated(DateFormat.getCurrentTime());
    permission.setViewPoints(permissionRequest.getViewPoints());
    permission.setSkipAccessability(1);
    if (permissionRequest.getFeatureId().size() != 0) {
      List<Feature> featureResponse = generateFeatureList(permissionRequest.getFeatureId());
      permission.setFeatureId(
          featureResponse.stream().map(Feature::get_id)
              .collect(Collectors.toList()));
    } else {
      permission.setFeatureId(new ArrayList<>());
    }
    if (permissionRequest.getUserId().size() != 0) {
      List<User> userResponse = generateUserList(permissionRequest.getUserId());
      permission
          .setUserId(
              userResponse.stream().map(User::get_id).collect(Collectors.toList()));
    } else {
      permission.setUserId(new ArrayList<>());
    }
    accessabilityRepository.addNewAccessability(
        new Accessability(null, new ObjectId(loginId), newId));
    repository.insertAndUpdate(permission);
  }

  @Override
  public void editPermission(PermissionRequest permissionRequest, String id) {
    Permission permission = repository.getPermissionById(id)
        .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.PERMISSION_NOT_FOUND));
    validate(permissionRequest);
    Map<String, String> error = generateError(PermissionRequest.class);
    permissionInventory.getPermissionByName(permissionRequest.getName()).ifPresent(perm -> {
      if (perm.get_id().compareTo(permission.get_id()) != 0) {
        error.put("name", LanguageMessageKey.INVALID_NAME_PERMISSION);
        throw new InvalidRequestException(error, LanguageMessageKey.INVALID_NAME_PERMISSION);
      }
    });
    permissionRequest.getUserId().forEach(userId -> {
      repository.getPermissionByUserId(userId).ifPresent(thisUser -> {
        error.put("userId", LanguageMessageKey.UNIQUE_USER_PERMISSION);
        throw new InvalidRequestException(error, LanguageMessageKey.UNIQUE_USER_PERMISSION);
      });
    });
    if (permission.getName().compareTo("super_admin_permission") == 0) {
      if (!permissionRequest.getUserId().contains(userInventory.findUserByUsername("super_admin")
          .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER))
          .get_id().toString())) {
        error.put("userId", LanguageMessageKey.MUST_CONTAIN_ADMIN_ACCOUNT);
        throw new InvalidRequestException(error, LanguageMessageKey.MUST_CONTAIN_ADMIN_ACCOUNT);
      }
      permission.setUserId(permissionRequest.getUserId().stream().map(ObjectId::new)
          .collect(Collectors.toList()));
      permission.setModified(DateFormat.getCurrentTime());
      repository.insertAndUpdate(permission);
    } else {
      permission.setName(permissionRequest.getName());
      if (permissionRequest.getFeatureId().size() != 0) {
        List<Feature> featureResponse = generateFeatureList(permissionRequest.getFeatureId());
        permission.setFeatureId(
            featureResponse.stream().map(Feature::get_id)
                .collect(Collectors.toList()));
      } else {
        permission.setFeatureId(new ArrayList<>());
      }
      if (permissionRequest.getUserId().size() != 0) {
        List<User> userResponse = generateUserList(permissionRequest.getUserId());
        permission
            .setUserId(
                userResponse.stream().map(User::get_id)
                    .collect(Collectors.toList()));
      } else {
        permission.setUserId(new ArrayList<>());
      }
      permission.setViewPoints(permissionRequest.getViewPoints());
      permission.setSkipAccessability(permissionRequest.getSkipAccessability());
      permission.setModified(DateFormat.getCurrentTime());
      repository.insertAndUpdate(permission);
    }

  }

  @Override
  public void deletePermission(String id) {
    Permission permission = repository.getPermissionById(id)
        .orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.PERMISSION_NOT_FOUND));
    if (permission.isCanDelete()) {
      repository.deletePermission(id);
    } else {
      throw new ForbiddenException(LanguageMessageKey.FORBIDDEN);
    }
  }

  @Override
  public Map<String, List<String>> getViewPointSelect() {
    return repository.getViewPointSelect();
  }

  private List<Feature> generateFeatureList(List<String> features) {
    String result = generateParamsValue(features);
    return featureRepository.getFeatures(Map.ofEntries(entry("_id", result.toString())), "", 0, 0,
        "").get();
  }

  private List<User> generateUserList(List<String> users) {
    String result = generateParamsValue(users);
    return userRepository.getUsers(Map.ofEntries(entry("_id", result.toString())), "", 0, 0, "")
        .get();
  }
}
