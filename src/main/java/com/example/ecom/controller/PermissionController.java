package com.example.ecom.controller;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.permission.PermissionRequest;
import com.example.ecom.dto.permission.PermissionResponse;
import com.example.ecom.service.permission.PermissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "permission")
public class PermissionController extends AbstractController<PermissionService> {

  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping(value = "get-list-permissions")
  public ResponseEntity<CommonResponse<ListWrapperResponse<PermissionResponse>>> getListPermissions(
      @RequestParam(required = false, defaultValue = "1") int page,
      @RequestParam(required = false, defaultValue = "10") int pageSize,
      @RequestParam Map<String, String> allParams,
      @RequestParam(defaultValue = "asc") String keySort,
      @RequestParam(defaultValue = "modified") String sortField, HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    if (allParams.containsKey("_id")) {
      checkAccessability(result.getLoginId(), allParams.get("_id"), result.isSkipAccessability());
    }
    return response(service.getPermissions(allParams, keySort, page, pageSize, sortField,
            result.isSkipAccessability(), result.getLoginId()),
        LanguageMessageKey.SUCCESS, result.getViewPoints()
            .get(PermissionResponse.class.getSimpleName()));
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping(value = "get-detail-permission")
  public ResponseEntity<CommonResponse<PermissionResponse>> getPermissionDetail(
      @RequestParam(required = true) String id,
      HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    System.out.println(result.isSkipAccessability());
    checkAccessability(result.getLoginId(), id, result.isSkipAccessability());
    return response(
        Optional.of(filterResponse(service.getPermissionById(id, result.isSkipAccessability(),
                result.getLoginId()).get(),
            result.getViewPoints())), LanguageMessageKey.SUCCESS, result.getViewPoints()
            .get(PermissionResponse.class.getSimpleName()));
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping(value = "get-view-points-select")
  public ResponseEntity<CommonResponse<Map<String, List<String>>>> getViewPointSelect() {
    return response(Optional.of(service.getViewPointSelect()), LanguageMessageKey.SUCCESS,
        new ArrayList<>());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PostMapping(value = "add-new-permission")
  public ResponseEntity<CommonResponse<String>> addNewPermission(
      @RequestBody PermissionRequest permissionRequest, HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    service.addNewPermissions(permissionRequest, result.getLoginId());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.ADD_PERMISSION_SUCCESS,
            HttpStatus.OK.value(), result.getViewPoints()
            .get(PermissionResponse.class.getSimpleName())),
        null,
        HttpStatus.OK.value());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping(value = "update-permission")
  public ResponseEntity<CommonResponse<String>> updatePermission(
      @RequestBody PermissionRequest permissionRequest, @RequestParam(required = true) String id,
      HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    checkAccessability(result.getLoginId(), id, result.isSkipAccessability());
    service.editPermission(permissionRequest, id);
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_PERMISSION_SUCCESS,
            HttpStatus.OK.value(), result.getViewPoints()
            .get(PermissionResponse.class.getSimpleName())),
        null,
        HttpStatus.OK.value());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PutMapping(value = "delete-permission")
  public ResponseEntity<CommonResponse<String>> deletePermission(
      @RequestParam(required = true) String id, HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    checkAccessability(result.getLoginId(), id, result.isSkipAccessability());
    service.deletePermission(id);
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.DELETE_PERMISSION_SUCCESS,
            HttpStatus.OK.value(), result.getViewPoints()
            .get(PermissionResponse.class.getSimpleName())),
        null,
        HttpStatus.OK.value());
  }
}
