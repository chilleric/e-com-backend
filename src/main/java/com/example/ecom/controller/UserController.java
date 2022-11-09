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
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.user.UserRequest;
import com.example.ecom.dto.user.UserResponse;
import com.example.ecom.service.user.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping(value = "user")
public class UserController extends AbstractController<UserService> {

        @SecurityRequirement(name = "Bearer Authentication")
        @PostMapping(value = "add-new-user")
        public ResponseEntity<CommonResponse<String>> addNewUser(@RequestBody UserRequest userRequest,
                        HttpServletRequest request) {
                validateToken(request, false);
                service.createNewUser(userRequest);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, "Create user successfully!",
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @GetMapping(value = "get-detail-user")
        public ResponseEntity<CommonResponse<UserResponse>> getUserDetail(@RequestParam(required = true) String id,
                        HttpServletRequest request) {
                ValidationResult result = validateToken(request, false);
                return response(service.findOneUserById(id,
                                getResponseType(id, result.getLoginId(), result.isSkipAccessability())), "Success");
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @GetMapping(value = "get-list-users")
        public ResponseEntity<CommonResponse<ListWrapperResponse<UserResponse>>> getListUsers(
                        @RequestParam(required = false, defaultValue = "1") int page,
                        @RequestParam(required = false, defaultValue = "10") int pageSize,
                        @RequestParam Map<String, String> allParams,
                        @RequestParam(defaultValue = "asc") String keySort,
                        @RequestParam(defaultValue = "modified") String sortField, HttpServletRequest request) {
                ValidationResult result = validateToken(request, false);
                return response(service.getUsers(allParams, keySort, page, pageSize, "",
                                getResponseType("", result.getLoginId(), result.isSkipAccessability())), "Success");
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PutMapping(value = "update-user")
        public ResponseEntity<CommonResponse<String>> updateUser(@RequestBody UserRequest userRequest,
                        @RequestParam(required = true) String id, HttpServletRequest request) {
                ValidationResult result = validateToken(request, false);
                checkUserId(id, result.getLoginId(), result.isSkipAccessability());
                service.updateUserById(id, userRequest);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, "Update user successfully!",
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

        @SecurityRequirement(name = "Bearer Authentication")
        @PutMapping(value = "change-status-user")
        public ResponseEntity<CommonResponse<String>> changeStatusUser(@RequestParam String id,
                        HttpServletRequest request) {
                ValidationResult result = validateToken(request, false);
                checkUserId(id, result.getLoginId(), result.isSkipAccessability());
                service.changeStatusUser(id);
                return new ResponseEntity<CommonResponse<String>>(
                                new CommonResponse<String>(true, null, "Delete user successfully!",
                                                HttpStatus.OK.value()),
                                null,
                                HttpStatus.OK.value());
        }

}
