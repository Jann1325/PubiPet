package io.github.jann1325.utils;

import io.github.jann1325.response.ApiResponse;

public class ApiResponseUtils {

    /**  成功（有資料） */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    /**  成功（無資料） */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "Success", null);
    }

    /**  自訂訊息的成功  */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /** 失敗回應（只回傳錯誤訊息） */
    public static ApiResponse<Void> fail(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }

    /** 失敗回應（回傳錯誤訊息＋錯誤資料） */
    public static <T> ApiResponse<T> fail(int status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }
}