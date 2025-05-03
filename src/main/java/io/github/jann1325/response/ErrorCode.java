package io.github.jann1325.response;

public enum ErrorCode {
    USER_NOT_FOUND(1001, "使用者不存在"),
    EMAIL_ALREADY_EXISTS(1002, "Email 已存在"),
    UNAUTHORIZED(1003, "未授權的請求"),
    FORBIDDEN(1004, "沒有權限"),
    PRODUCT_NOT_FOUND(1005, "找不到商品"),
    CART_EMPTY(1006, "購物車是空的"),
    INVALID_USER_INPUT(1007, "使用者輸入資料有誤"),
    TOO_MANY_REQUESTS(1008, "請求過於頻繁，請稍後再試"),
    EMAIL_NOT_REGISTERED(1009, "Email 尚未註冊，請前往註冊"),
    SERVER_ERROR(1500, "伺服器錯誤");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
