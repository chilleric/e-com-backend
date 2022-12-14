package com.example.ecom.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.constant.ResponseType;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;
import com.example.ecom.repository.common_entity.ViewPoint;
import com.example.ecom.utils.ObjectValidator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractService<r> {

  @Autowired
  protected r repository;

  @Autowired
  protected Environment env;

  @Autowired
  protected ObjectValidator objectValidator;

  protected ObjectMapper objectMapper;

  protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

  @PostConstruct
  public void init() {
    objectMapper =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  protected String generateParamsValue(List<String> list) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      result.append(list.get(i));
      if (i != list.size() - 1) {
        result.append(",");
      }
    }
    return result.toString();
  }

  protected BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  protected <T> void validate(T request) {
    boolean isError = false;
    Map<String, String> errors = objectValidator
        .validateRequestThenReturnMessage(generateError(request.getClass()), request);
    for (Map.Entry<String, String> items : errors.entrySet()) {
      if (items.getValue().length() > 0) {
        isError = true;
        break;
      }
    }
    if (isError) {
      throw new InvalidRequestException(errors, LanguageMessageKey.INVALID_REQUEST);
    }
  }

  protected <T> T viewPointToRequest(T request, List<ViewPoint> viewPoints, Object source) {
    for (Field field : request.getClass().getDeclaredFields()) {
      boolean isEditable = false;
      field.setAccessible(true);
      for (ViewPoint thisView : viewPoints) {
        if (thisView.getKey().compareTo(field.getName()) == 0) {
          isEditable = true;
          break;
        }
      }
      if (!isEditable) {
        for (Field field1 : source.getClass().getDeclaredFields()) {
          field1.setAccessible(true);
          if (field1.getName().compareTo(field.getName()) == 0) {
            try {
              field.set(request, field1.get(source));
            } catch (IllegalArgumentException | IllegalAccessException e) {
              APP_LOGGER.error(e.getMessage());
              throw new BadSqlException(LanguageMessageKey.SERVER_ERROR);
            }
          }
        }
      }
    }
    return request;
  }

  protected Map<String, String> generateError(Class<?> clazz) {
    Field[] fields = clazz.getDeclaredFields();
    Map<String, String> result = new HashMap<>();
    for (Field field : fields) {
      result.put(field.getName(), "");
    }
    return result;
  }

  protected ResponseType isPublic(String ownerId, String loginId, boolean skipAccessability) {
    if (skipAccessability) {
      return ResponseType.PRIVATE;
    }
    if (ownerId.compareTo(loginId) == 0) {
      return ResponseType.PRIVATE;
    } else {
      return ResponseType.PUBLIC;
    }
  }
}
