package com.mininstagram.common;
import org.springframework.http.HttpStatus;
public class BusinessException extends RuntimeException {
  private final HttpStatus status; private final String errorCode;
  public BusinessException(HttpStatus status, String code, String detail) {
    super(detail); this.status=status; this.errorCode=code;
  }
  public HttpStatus status() { return status; }
  public String errorCode() { return errorCode; }
}
