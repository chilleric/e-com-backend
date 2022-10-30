package com.example.ecom.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.permission.PermissionRequest;
import com.example.ecom.dto.permission.PermissionResponse;
import com.example.ecom.service.permission.PermissionService;

@RestController
@RequestMapping(value = "permission")
public class PermissionController extends AbstractController<PermissionService> {

    @GetMapping(value = "get-list-permissions")
    public ResponseEntity<CommonResponse<ListWrapperResponse<PermissionResponse>>> getFeatures(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "asc") String keySort,
            @RequestParam(defaultValue = "modified") String sortField, HttpServletRequest request) {
        return response(service.getPermissions(allParams, keySort, page, pageSize, sortField),
                "Get list of permissions successfully!");
    }

    @PostMapping(value = "add-new-permission")
    public ResponseEntity<CommonResponse<String>> addNewFeature(
            @RequestBody PermissionRequest permissionRequest, HttpServletRequest request) {
        service.addNewPermissions(permissionRequest);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Add permission successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @PutMapping(value = "update-permission")
    public ResponseEntity<CommonResponse<String>> updateFeature(
            @RequestBody PermissionRequest permissionRequest, @RequestParam(required = true) String id,
            HttpServletRequest request) {
        service.editPermission(permissionRequest, id);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Update permission successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @DeleteMapping(value = "delete-permission")
    public ResponseEntity<CommonResponse<String>> updateFeature(
            @RequestParam(required = true) String id, HttpServletRequest request) {
        service.deletePermission(id);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Delete permission successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }
}
