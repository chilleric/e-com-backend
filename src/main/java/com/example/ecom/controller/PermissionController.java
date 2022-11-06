package com.example.ecom.controller;

import java.util.Map;

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

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.permission.PermissionRequest;
import com.example.ecom.dto.permission.PermissionResponse;
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
                validateToken(request, false);
                return response(service.getPermissions(allParams, keySort, page, pageSize, sortField),
                                "Get list of permissions successfully!");
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PostMapping(value = "add-new-permission")
        public ResponseEntity<CommonResponse<String>> addNewPermission(
                        @RequestBody PermissionRequest permissionRequest, HttpServletRequest request) {
                validateToken(request, false);
                service.addNewPermissions(permissionRequest);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, "Add permission successfully!",
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PutMapping(value = "update-permission")
        public ResponseEntity<CommonResponse<String>> updatePermission(
                        @RequestBody PermissionRequest permissionRequest, @RequestParam(required = true) String id,
                        HttpServletRequest request) {
                validateToken(request, false);
                service.editPermission(permissionRequest, id);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, "Update permission successfully!",
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PutMapping(value = "delete-permission")
        public ResponseEntity<CommonResponse<String>> deletePermission(
                        @RequestParam(required = true) String id, HttpServletRequest request) {
                validateToken(request, false);
                service.deletePermission(id);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, "Delete permission successfully!",
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }
}
