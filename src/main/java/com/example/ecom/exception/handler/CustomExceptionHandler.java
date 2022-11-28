package com.example.ecom.exception.handler;

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.exception.ForbiddenException;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.exception.UnauthorizedException;
import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

  private static final AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

  @ExceptionHandler(BadSqlException.class)
  public ResponseEntity<CommonResponse<String>> handleBadSqlException(BadSqlException e) {
    APP_LOGGER.error(e.getMessage());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(false, null, e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(), new ArrayList<>()),
        null,
        HttpStatus.OK.value());
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<CommonResponse<Map<String, String>>> handleInvalidRequestException(
      InvalidRequestException e) {
    APP_LOGGER.error(e.getMessage());
    return new ResponseEntity<>(
        new CommonResponse<Map<String, String>>(false, e.getResult(), e.getMessage(),
            HttpStatus.BAD_REQUEST.value(), new ArrayList<>()),
        null,
        HttpStatus.OK.value());
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<CommonResponse<String>> handleForbidden(ForbiddenException e) {
    APP_LOGGER.error(e.getMessage());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(false, null, e.getMessage(), HttpStatus.FORBIDDEN.value(),
            new ArrayList<>()),
        null,
        HttpStatus.OK.value());
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<CommonResponse<String>> handleUnAuthorizedException(
      UnauthorizedException e) {
    APP_LOGGER.error(e.getMessage());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(false, null, e.getMessage(),
            HttpStatus.UNAUTHORIZED.value(), new ArrayList<>()),
        null,
        HttpStatus.OK.value());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<CommonResponse<String>> handleResourceNotFoundException(
      ResourceNotFoundException e) {
    APP_LOGGER.error(e.getMessage());
    return new ResponseEntity<>(
        new CommonResponse<String>(false, null, e.getMessage(), HttpStatus.NOT_FOUND.value(),
            new ArrayList<>()),
        null,
        HttpStatus.OK.value());
  }
}
