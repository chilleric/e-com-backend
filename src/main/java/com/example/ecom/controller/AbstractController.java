package com.example.ecom.controller;

import static java.util.Map.entry;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import com.example.ecom.constant.ResponseType;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.exception.ForbiddenException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.exception.UnauthorizedException;
import com.example.ecom.jwt.JwtValidation;
import com.example.ecom.jwt.TokenContent;
import com.example.ecom.repository.accessability.AccessabilityRepository;
import com.example.ecom.repository.feature.Feature;
import com.example.ecom.repository.feature.FeatureRepository;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;

public abstract class AbstractController<s> {
    @Autowired
    protected s service;

    @Autowired
    protected JwtValidation jwtValidation;

    @Autowired
    protected FeatureRepository featureRepository;

    @Autowired
    protected PermissionRepository permissionRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private AccessabilityRepository accessabilityRepository;

    protected ValidationResult validateToken(HttpServletRequest request, boolean hasPublic) {
        String token = jwtValidation.getJwtFromRequest(request);
        if (token == null) {
            if (!hasPublic) {
                throw new UnauthorizedException("Unauthorized");
            }
            return new ValidationResult(false, "public");
        }
        return checkAuthentication(token, request.getRequestURI(), true);
    }

    protected ValidationResult validateSSE(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return checkAuthentication(token.substring(7), "", false);
        } else {
            throw new UnauthorizedException("Unauthorized");
        }

    }

    protected ValidationResult checkAuthentication(String token, String path, boolean checkPath) {
        TokenContent info = jwtValidation.getUserIdFromJwt(token);
        List<User> user = userRepository
                .getUsers(Map.ofEntries(entry("_id", info.getUserId()), entry("deleted", "0")), "", 0, 0, "").get();
        if (user.size() == 0) {
            throw new UnauthorizedException("User are deactivated or deleted!");
        }
        if (!user.get(0).getTokens().containsKey(info.getDeviceId())) {
            throw new UnauthorizedException("Unauthorized!");
        }
        Date now = new Date();
        if (user.get(0).getTokens().get(info.getDeviceId()).compareTo(now) <= 0) {
            throw new UnauthorizedException("Unauthorized!");
        }
        if (checkPath) {
            List<Feature> feature = featureRepository
                    .getFeatures(Map.ofEntries(entry("path", path)), "", 0, 0, "").get();
            if (feature.size() == 0) {
                throw new ResourceNotFoundException("This feature is not enabled!");
            }
            List<Permission> permissions = permissionRepository
                    .getPermissionByUser(user.get(0).get_id().toString(), feature.get(0).get_id().toString())
                    .get();
            if (permissions.size() == 0) {
                throw new ForbiddenException("You are not approved any permissions!");
            }
            boolean skipAccessability = false;
            for (Permission permission : permissions) {
                if (permission.getSkipAccessability() == 0
                        && permission.getFeatureId().contains(feature.get(0).get_id())) {
                    skipAccessability = true;
                }
            }
            return new ValidationResult(skipAccessability, user.get(0).get_id().toString());
        }
        return new ValidationResult(false, user.get(0).get_id().toString());

    }

    protected ResponseType getResponseType(String ownerId, String loginId, boolean skipAccessability) {
        if (skipAccessability)
            return ResponseType.PRIVATE;
        if (ownerId.compareTo(loginId) == 0) {
            return ResponseType.PRIVATE;
        } else
            return ResponseType.PUBLIC;
    }

    protected <T> ResponseEntity<CommonResponse<T>> response(Optional<T> response, String successMessage) {
        return new ResponseEntity<>(new CommonResponse<>(true, response.get(), successMessage, HttpStatus.OK.value()),
                HttpStatus.OK);
    }

    protected void checkUserId(String userId, String loginId, boolean skipAccessability) {
        if (!skipAccessability) {
            if (loginId.compareTo(userId) == 0) {
                throw new ForbiddenException("Access denied!");
            }
        }
    }

    protected void checkAccessability(String loginId, String targetId, boolean skipAccessability) {
        if (!skipAccessability) {
            accessabilityRepository.getAccessability(loginId, targetId)
                    .orElseThrow(() -> new ForbiddenException("Access denied!"));
        }
    }
}
