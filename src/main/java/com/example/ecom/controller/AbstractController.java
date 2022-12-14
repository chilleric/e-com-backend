package com.example.ecom.controller;

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

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.exception.ForbiddenException;
import com.example.ecom.exception.UnauthorizedException;
import com.example.ecom.inventory.user.UserInventory;
import com.example.ecom.jwt.JwtValidation;
import com.example.ecom.jwt.TokenContent;
import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;
import com.example.ecom.repository.accessability.AccessabilityRepository;
import com.example.ecom.repository.common_entity.ViewPoint;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.utils.ObjectUtilities;

public abstract class AbstractController<s> {

	@Autowired
	protected s service;

	@Autowired
	protected JwtValidation jwtValidation;

	@Autowired
	protected PermissionRepository permissionRepository;

	@Autowired
	protected UserInventory userInventory;

	@Autowired
	private AccessabilityRepository accessabilityRepository;

	protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

	protected ValidationResult validateToken(HttpServletRequest request) {
		String token = jwtValidation.getJwtFromRequest(request);
		if (token == null) {
			throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
		}
		return checkAuthentication(token);
	}

	protected ValidationResult validateSSE(String token) {
		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			return checkAuthentication(token.substring(7));
		} else {
			throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
		}

	}

	protected ValidationResult checkAuthentication(String token) {
		TokenContent info = jwtValidation.getUserIdFromJwt(token);
		User user = userInventory.getActiveUserById(info.getUserId())
				.orElseThrow(() -> new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED));
		if (!user.getTokens().containsKey(info.getDeviceId())) {
			APP_LOGGER.error("not found deviceid authen");
			throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
		}
		Date now = new Date();
		if (user.getTokens().get(info.getDeviceId()).compareTo(now) <= 0) {
			APP_LOGGER.error("not found expired device authen");
			throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
		}
		Map<String, List<ViewPoint>> thisView = new HashMap<>();
		Map<String, List<ViewPoint>> thisEdit = new HashMap<>();
		permissionRepository.getPermissionByUserId(user.get_id().toString())
				.orElseThrow(() -> new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED))
				.forEach(thisPerm -> {
					thisView.putAll(
							ObjectUtilities.mergePermission(thisView, thisPerm.getViewPoints()));
					thisEdit.putAll(
							ObjectUtilities.mergePermission(thisEdit, thisPerm.getEditable()));
				});
		return new ValidationResult(user.get_id().toString(), thisView, thisEdit);

	}

	protected <T> ResponseEntity<CommonResponse<T>> response(Optional<T> response,
			String successMessage, List<ViewPoint> viewPoint, List<ViewPoint> editable) {
		return new ResponseEntity<>(new CommonResponse<>(true, response.get(), successMessage,
				HttpStatus.OK.value(), viewPoint, editable), HttpStatus.OK);
	}

	protected void checkAccessability(String loginId, String targetId) {
		if (loginId.compareTo(targetId) != 0) {
			accessabilityRepository.getAccessability(loginId, targetId)
					.orElseThrow(() -> new ForbiddenException(LanguageMessageKey.FORBIDDEN));
		}
	}

	protected <T> T filterResponse(T input, Map<String, List<ViewPoint>> compares) {
		List<ViewPoint> compareList = new ArrayList<>();
		compares.forEach((key, value) -> {
			if (key.compareTo(input.getClass().getSimpleName()) == 0) {
				compareList.addAll(value);
			}
		});
		for (Field field : input.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				boolean isExist = false;
				for (ViewPoint thisItem : compareList) {
					if (field.getName().compareTo("id") == 0) {
						isExist = true;
						break;
					}
					if (thisItem.getKey().compareTo(field.getName()) == 0) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
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
