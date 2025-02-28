package com.toongather.toongather.global.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@AllArgsConstructor
public enum CommonError {

  //auth
  COMMON_AUTH_ERROR(HttpStatus.UNAUTHORIZED, "AUTH_ERROR"),
  JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT_REFRESH NEED"),
  JWT_DENIED(HttpStatus.UNAUTHORIZED, "JWT_DENIED"),
  USER_NOT_PASSWORD(HttpStatus.UNAUTHORIZED, "NOT_PASSWORD"),
  USER_NOT_ACTIVE(HttpStatus.UNAUTHORIZED,  "NOT_ACTIVE_USER"),

  //validation
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR")

  ;
  private final HttpStatus status;
  private final String message;

}
