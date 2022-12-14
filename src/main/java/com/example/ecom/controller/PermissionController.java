package com.example.ecom.controller;

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
import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.permission.PermissionRequest;
import com.example.ecom.dto.permission.PermissionResponse;
import com.example.ecom.repository.common_entity.ViewPoint;
import com.example.ecom.service.permission.PermissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
        ValidationResult result = validateToken(request);
        return response(
                service.getPermissions(allParams, keySort, page, pageSize, sortField,
                        result.getLoginId()),
                LanguageMessageKey.SUCCESS, service.getPermissionView(),
                service.getPermissionView());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-detail-permission")
    public ResponseEntity<CommonResponse<PermissionResponse>> getPermissionDetail(
            @RequestParam(required = true) String id, HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        checkAccessability(result.getLoginId(), id);
        return response(service.getPermissionById(id, result.getLoginId()),
                LanguageMessageKey.SUCCESS, service.getPermissionView(),
                service.getPermissionView());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-view-points-select")
    public ResponseEntity<CommonResponse<Map<String, List<ViewPoint>>>> getViewPointSelect(
            HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        return response(Optional.of(service.getViewPointSelect(result.getLoginId())),
                LanguageMessageKey.SUCCESS, service.getPermissionView(),
                service.getPermissionView());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-editable-select")
    public ResponseEntity<CommonResponse<Map<String, List<ViewPoint>>>> getEditableSelect(
            HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        return response(Optional.of(service.getEditableSelect(result.getLoginId())),
                LanguageMessageKey.SUCCESS, service.getPermissionView(),
                service.getPermissionView());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "add-new-permission")
    public ResponseEntity<CommonResponse<String>> addNewPermission(
            @RequestBody PermissionRequest permissionRequest, HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        service.addNewPermissions(permissionRequest, result.getLoginId());
        return new ResponseEntity<CommonResponse<String>>(new CommonResponse<String>(true, null,
                LanguageMessageKey.ADD_PERMISSION_SUCCESS, HttpStatus.OK.value(),
                service.getPermissionView(), service.getPermissionView()), null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "update-permission")
    public ResponseEntity<CommonResponse<String>> updatePermission(
            @RequestBody PermissionRequest permissionRequest,
            @RequestParam(required = true) String id, HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        checkAccessability(result.getLoginId(), id);
        service.editPermission(permissionRequest, id,
                result.getEditable().get(PermissionResponse.class.getSimpleName()));
        return new ResponseEntity<CommonResponse<String>>(new CommonResponse<String>(true, null,
                LanguageMessageKey.UPDATE_PERMISSION_SUCCESS, HttpStatus.OK.value(),
                service.getPermissionView(), service.getPermissionView()), null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "delete-permission")
    public ResponseEntity<CommonResponse<String>> deletePermission(
            @RequestParam(required = true) String id, HttpServletRequest request) {
        ValidationResult result = validateToken(request);
        checkAccessability(result.getLoginId(), id);
        service.deletePermission(id);
        return new ResponseEntity<CommonResponse<String>>(new CommonResponse<String>(true, null,
                LanguageMessageKey.DELETE_PERMISSION_SUCCESS, HttpStatus.OK.value(),
                service.getPermissionView(), service.getPermissionView()), null,
                HttpStatus.OK.value());
    }
}
