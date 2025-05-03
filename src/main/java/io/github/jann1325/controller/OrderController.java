package io.github.jann1325.controller;


import io.github.jann1325.dto.OrderDTO;
import io.github.jann1325.response.ApiResponse;
import io.github.jann1325.service.OrderService;
import io.github.jann1325.utils.ApiResponseUtils;
import io.github.jann1325.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    // 建立訂單
    @PostMapping("/submit")
    public ApiResponse<Void> submitOrder(@RequestHeader("Authorization") String authorization,
                                         @RequestBody OrderDTO.OrderDataDTO orderDataDTO) {
        orderService.submitOrder(authorization, orderDataDTO);
        return ApiResponseUtils.success();
    }

    // 查詢歷史訂單
    @GetMapping("/history")
    public ApiResponse<List<OrderDTO.OrderHistoryDTO>> getOrderHistory(@RequestHeader("Authorization") String authorizationHeader) {
        return ApiResponseUtils.success(orderService.getOrderHistory(authorizationHeader));
    }
}
