package io.github.jann1325.controller;

import io.github.jann1325.response.ApiResponse;
import io.github.jann1325.service.UserService;
import io.github.jann1325.utils.ApiResponseUtils;
import io.github.jann1325.utils.JwtUtils;
import io.github.jann1325.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 取得用戶基本資料
    @GetMapping("/getUserProfile")
    public ApiResponse<UserDTO.ProfileDTO> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        return ApiResponseUtils.success(userService.getUserProfile(authorizationHeader));
    }

    // 更新用戶基本資料
    @PostMapping("/updateUserProfile")
    public ApiResponse<Void> updateUserProfile(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody UserDTO.ProfileDTO profileDTO) {
        userService.updateUserProfile(authorizationHeader, profileDTO);
        return ApiResponseUtils.success();
    }

    // 取得用戶地址資料
    @GetMapping("/getUserAddress")
    public ApiResponse<List<UserDTO.AddressDTO>> getUserAddress (@RequestHeader("Authorization") String authorizationHeader) {
        return ApiResponseUtils.success(userService.getUserAddress(authorizationHeader));
    }

    // 更新用戶地址資料
    @PostMapping("/updateUserAddress")
    public ApiResponse<Void> updateUserAddress(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody UserDTO.AddressDTO addressDTO) {
        userService.updateUserAddress(authorizationHeader, addressDTO);
        return ApiResponseUtils.success();
    }

    @PostMapping("/deleteUserAddress")
    public ApiResponse<Void> deleteUserAddress(@RequestBody Map<String, Object> request) {
        Integer addressId = (Integer) request.get("addressId");
        userService.deleteUserAddress(addressId);
        return ApiResponseUtils.success();
    }

}
