package com.talkit.app.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessLogicException extends RuntimeException {
  private ExceptionType exceptionType;
  private String message;

  public BusinessLogicException(ExceptionType exceptionType) {
    this.exceptionType = exceptionType;
    this.message = exceptionType.getDefaultMessage();
  }

  public BusinessLogicException(ExceptionType exceptionType, String message) {
    this.exceptionType = exceptionType;
    this.message = message;
  }

  public boolean isMessageNotEmpty() {
    return this.message != null && !this.message.isEmpty();
  }

  public boolean isInternalServerError() {
    return this.exceptionType.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
