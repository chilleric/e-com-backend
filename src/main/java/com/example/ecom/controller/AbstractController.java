package com.example.ecom.controller;

import static java.util.Map.entry;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.constant.ResponseType;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.exception.ForbiddenException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.exception.UnauthorizedException;
import com.example.ecom.jwt.JwtValidation;
import com.example.ecom.jwt.TokenContent;
import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;
import com.example.ecom.repository.accessability.AccessabilityRepository;
import com.example.ecom.repository.feature.Feature;
import com.example.ecom.repository.feature.FeatureRepository;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

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

  protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

  protected ValidationResult validateToken(HttpServletRequest request, boolean hasPublic) {
    String token = jwtValidation.getJwtFromRequest(request);
    if (token == null) {
      if (!hasPublic) {
        throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
      }
      return new ValidationResult(false, "public", new HashMap<>());
    }
    return checkAuthentication(token, request.getRequestURI(), true);
  }

  protected ValidationResult validateSSE(String token) {
    if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
      return checkAuthentication(token.substring(7), "", false);
    } else {
      throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
    }

  }

  protected ValidationResult checkAuthentication(String token, String path, boolean checkPath) {
    TokenContent info = jwtValidation.getUserIdFromJwt(token);
    List<User> user = userRepository
        .getUsers(Map.ofEntries(entry("_id", info.getUserId()), entry("deleted", "0")), "", 0, 0,
            "").get();
    if (user.size() == 0) {
      APP_LOGGER.error("not found user authen");
      throw new UnauthorizedException(LanguageMessageKey.NOT_FOUND_USER);
    }
    if (!user.get(0).getTokens().containsKey(info.getDeviceId())) {
      APP_LOGGER.error("not found deviceid authen");
      APP_LOGGER.error("info" + info);
      APP_LOGGER.error("user" + user);
      throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
    }
    Date now = new Date();
    if (user.get(0).getTokens().get(info.getDeviceId()).compareTo(now) <= 0) {
      APP_LOGGER.error("not found expired device authen");
      throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
    }
    if (checkPath) {
      List<Feature> feature = featureRepository
          .getFeatures(Map.ofEntries(entry("path", path)), "", 0, 0, "").get();
      if (feature.size() == 0) {
        throw new ResourceNotFoundException(LanguageMessageKey.DISABLED_FEATURE);
      }
      Permission permissions = permissionRepository
          .getPermissionByUser(user.get(0).get_id().toString(), feature.get(0).get_id().toString())
          .orElseThrow(() -> new ForbiddenException(LanguageMessageKey.FORBIDDEN));
      return new ValidationResult(permissions.getSkipAccessability() == 0,
          user.get(0).get_id().toString(),
          permissions.getViewPoints());
    }
    return new ValidationResult(false, user.get(0).get_id().toString(), new HashMap<>());

  }

  protected ResponseType getResponseType(String ownerId, String loginId,
      boolean skipAccessability) {
    if (skipAccessability) {
      return ResponseType.PRIVATE;
    }
    if (ownerId.compareTo(loginId) == 0) {
      return ResponseType.PRIVATE;
    } else {
      return ResponseType.PUBLIC;
    }
  }

  protected <T> ResponseEntity<CommonResponse<T>> response(Optional<T> response,
      String successMessage, List<String> viewPoint) {
    return new ResponseEntity<>(
        new CommonResponse<>(true, response.get(), successMessage, HttpStatus.OK.value(),
            viewPoint),
        HttpStatus.OK);
  }

  protected void checkUserId(String userId, String loginId, boolean skipAccessability) {
    if (!skipAccessability) {
      if (loginId.compareTo(userId) == 0) {
        throw new ForbiddenException(LanguageMessageKey.FORBIDDEN);
      }
    }
  }

  protected void checkAccessability(String loginId, String targetId, boolean skipAccessability) {
    if (!skipAccessability) {
      accessabilityRepository.getAccessability(loginId, targetId)
          .orElseThrow(() -> new ForbiddenException(LanguageMessageKey.FORBIDDEN));
    }
  }

  protected <T> T filterResponse(T input, Map<String, List<String>> compares) {
    List<String> compareList = new ArrayList<>();
    compares.forEach((key, value) -> {
      if (key.compareTo(input.getClass().getSimpleName()) == 0) {
        compareList.addAll(value);
      }
    });
    for (Field field : input.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        if (!compareList.contains(field.getName())) {
          if (field.getType() == String.class) {
            field.set(input, "");
          }
          if (field.getType() == int.class) {
            field.set(input, 0);
          }
          if (field.getType() == boolean.class) {
            field.set(input, false);
          }
          if (field.getType() == Map.class) {
            field.set(input, new HashMap<>());
          }
          if (field.getType() == List.class) {
            field.set(input, new ArrayList<>());
          }
        }
      } catch (Exception e) {
        throw new BadSqlException(LanguageMessageKey.SERVER_ERROR);
      }
    }
    return input;
  }
}
