package io.github.jann1325.exception;

import io.github.jann1325.response.ApiResponse;
import io.github.jann1325.response.ErrorCode;
import io.github.jann1325.utils.ApiResponseUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ApiResponse<Void> handleApiException(ApiException ex, HttpServletResponse response) {
        response.setStatus(ex.getHttpStatus().value());
        return ApiResponseUtils.fail(
                ex.getErrorCode().getCode(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex, HttpServletResponse response) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ApiResponseUtils.fail(
                ErrorCode.SERVER_ERROR.getCode(),
                "伺服器出錯了，請稍後再試。"
        );
    }
}
