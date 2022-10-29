package com.example.ecom.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.userDTO.UserRequest;
import com.example.ecom.dto.userDTO.UserResponse;
import com.example.ecom.service.userService.UserService;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "user")
public class UserController extends AbstractController<UserService> {
    @PostMapping(value = "add-new-user")
    public ResponseEntity<CommonResponse<String>> addNewUser(@RequestBody UserRequest userRequest,
            HttpServletRequest request) {
        service.createNewUser(userRequest);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Create user successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @GetMapping(value = "get-detail-user")
    public ResponseEntity<CommonResponse<UserResponse>> getUserById(@RequestParam(required = true) String id) {
        return response(service.findOneUserById(id), "Success");
    }

    @GetMapping(value = "get-list-users")
    public ResponseEntity<CommonResponse<ListWrapperResponse<UserResponse>>> getUsers(
        @RequestParam(required = false, defaultValue = "1") int page,
                        @RequestParam(required = false, defaultValue = "10") int pageSize,
                        @RequestParam Map<String, String> allParams,
                        @RequestParam(defaultValue = "asc") String keySort,
                        @RequestParam(defaultValue = "modified") String sortField, HttpServletRequest request
    ) {
        return response(service.getUsers(allParams, keySort, page, pageSize, ""), "Success");
    }

    @PostMapping(value = "update-user")
    public ResponseEntity<CommonResponse<String>> updateUser(@RequestBody UserRequest userRequest,
            @RequestParam(required = true) String userId) {
        service.updateUserById(userId, userRequest);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Update user successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @DeleteMapping(value = "delete-user")
    public ResponseEntity<CommonResponse<String>> deleteUser(@RequestParam String userId) {
        service.deleteUserById(userId);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Delete user successfully!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

}
