package com.example.ecom.controller;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.user.UserRequest;
import com.example.ecom.dto.user.UserResponse;
import com.example.ecom.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = "user")
public class UserController extends AbstractController<UserService> {

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "add-new-user")
    public ResponseEntity<CommonResponse<String>> addNewUser(@RequestBody UserRequest userRequest,
                                                             HttpServletRequest request) {
        ValidationResult result = validateToken(request, false);
        service.createNewUser(userRequest, result.getLoginId());
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.CREATE_USER_SUCCESS,
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "get-detail-user")
    public ResponseEntity<CommonResponse<UserResponse>> getUserDetail(@RequestParam(required = true) String id,
                                                                      HttpServletRequest request) {
        ValidationResult result = validateToken(request, false);
        checkAccessability(result.getLoginId(), id, result.isSkipAccessability());
        return response(service.findOneUserById(id,
                getResponseType(id, result.getLoginId(), result.isSkipAccessability())), LanguageMessageKey.SUCCESS);
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
                getResponseType("", result.getLoginId(), result.isSkipAccessability())), LanguageMessageKey.SUCCESS);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "update-user")
    public ResponseEntity<CommonResponse<String>> updateUser(@RequestBody UserRequest userRequest,
                                                             @RequestParam(required = true) String id, HttpServletRequest request) {
        ValidationResult result = validateToken(request, false);
        checkUserId(id, result.getLoginId(), result.isSkipAccessability());
        checkAccessability(result.getLoginId(), id, result.isSkipAccessability());
        service.updateUserById(id, userRequest);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.UPDATE_USER_SUCCESS,
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
        checkAccessability(result.getLoginId(), id, result.isSkipAccessability());
        service.changeStatusUser(id);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, LanguageMessageKey.CHANGE_STATUS_USER_SUCCESS,
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

}
