package io.github.jann1325.controller;

import io.github.jann1325.dto.UserDTO;
import io.github.jann1325.response.ApiResponse;
import io.github.jann1325.service.ProductService;
import io.github.jann1325.dto.ProductDTO;
import io.github.jann1325.utils.ApiResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 查詢商品列表（分頁）
    @GetMapping("/getProducts")
    public ApiResponse<Map<String, Object>> findProducts(@RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                         @RequestParam(value = "keyword", required = false) String keyword,
                                                         @RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer limit,
                                                         @RequestParam(defaultValue = "prod_name") String sort) {
        return ApiResponseUtils.success(productService.findProducts(categoryId, keyword, page, limit, sort));
    }

    // 查詢單項商品資料
    @GetMapping("/getProduct")
    public ApiResponse<ProductDTO.ProductInfoDTO> findProduct(@RequestParam(value = "productId") Integer id) {
        return ApiResponseUtils.success(productService.getProductInfo(id));
    }

    // 查詢購物車中的商品資料
    @GetMapping("/getCartProducts")
    public ApiResponse<List<ProductDTO.ProductListDTO>> findProductsByIds(@RequestParam List<Integer> productIds) {
        return ApiResponseUtils.success(productService.findProductsByIds(productIds));
    }
}

