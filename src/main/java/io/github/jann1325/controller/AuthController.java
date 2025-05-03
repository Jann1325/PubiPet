package io.github.jann1325.controller;

import io.github.jann1325.exception.ApiException;
import io.github.jann1325.response.ApiResponse;
import io.github.jann1325.service.AuthService;
import io.github.jann1325.dto.UserDTO;
import io.github.jann1325.service.UserService;
import io.github.jann1325.utils.ApiResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    // 根據 email 查詢用戶資料
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody UserDTO.LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    // 註冊
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody UserDTO.RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ApiResponseUtils.success();
    }

    // 驗證 email
    @GetMapping("/verifyEmail")
    public ApiResponse<String> verifyEmail(@RequestParam(value = "emailToken") String emailToken) {
            return ApiResponseUtils.success(authService.verifyLoginToken(emailToken));
    }

    @PostMapping("/validateToken")
    public ApiResponse<Void> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        authService.validateToken(authorizationHeader);
        return ApiResponseUtils.success();
    }
}
