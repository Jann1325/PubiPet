package io.github.jann1325.controller;

import io.github.jann1325.dto.CartDTO;
import io.github.jann1325.response.ApiResponse;
import io.github.jann1325.service.CartService;
import io.github.jann1325.utils.ApiResponseUtils;
import io.github.jann1325.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    // 查詢購物車商品數量
    @GetMapping("/count")
    public ApiResponse<Integer> getCartCount(@RequestHeader("Authorization") String authorizationHeader) {
        return ApiResponseUtils.success(cartService.getCartCount(authorizationHeader));
    }

    // 同步購物車資料
    @PostMapping("/sync")
    public ApiResponse<Void> syncCart(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody List<CartDTO> cartDTOList) {
        cartService.syncCart(authorizationHeader, cartDTOList);
        return ApiResponseUtils.success();
    }

    // 取得購物車資料
    @GetMapping("/getCartItem")
    public ApiResponse<List<Map<String, Object>>> getCartItems(@RequestHeader("Authorization") String authorizationHeader) {
            return ApiResponseUtils.success(cartService.getCartItems(authorizationHeader));

    }

    // 加入購物車
    @PostMapping("/addToCart")
    public ApiResponse<Void> addToCart(@RequestHeader("Authorization") String authorizationHeader,
                                       @RequestBody CartDTO cartDTO) {
        cartService.addItem(authorizationHeader, cartDTO);
        return ApiResponseUtils.success();
    }

    // 刪除購物車資料
    @DeleteMapping("/deleteItems")
    public ApiResponse<?> deleteItems(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody Map<String, List<Integer>> req) {
        cartService.deleteItems(authorizationHeader, req.get("productIds"));
        return ApiResponseUtils.success();
    }

    // 更新購物車商品的數量
    @PutMapping("/updateQuantities")
    public ApiResponse<Void>  updateQuantities(@RequestHeader("Authorization") String authorizationHeader,
                                                              @RequestBody List<CartDTO> cartDTOList) {
        cartService.updateQuantities(authorizationHeader, cartDTOList);
        return ApiResponseUtils.success();
    }
}
