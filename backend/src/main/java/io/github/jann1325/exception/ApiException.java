package io.github.jann1325.exception;

import io.github.jann1325.response.ErrorCode;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;

    public ApiException(ErrorCode errorCode) {
        this(errorCode, HttpStatus.BAD_REQUEST, null);
    }

    public ApiException(ErrorCode errorCode, HttpStatus httpStatus) {
        this(errorCode, httpStatus, null);
    }

    public ApiException(ErrorCode errorCode, String message) {
        this(errorCode, HttpStatus.BAD_REQUEST, message);
    }

    public ApiException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        super(message != null ? message : errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus != null ? httpStatus : HttpStatus.BAD_REQUEST;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
