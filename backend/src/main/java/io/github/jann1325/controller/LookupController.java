package io.github.jann1325.controller;

import io.github.jann1325.dto.LookupDTO;
import io.github.jann1325.dto.ProductCategoryDTO;
import io.github.jann1325.response.ApiResponse;
import io.github.jann1325.service.LookupService;
import io.github.jann1325.utils.ApiResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/lookup")
public class LookupController {

    @Autowired
    private LookupService lookupService;

    // 取得商品分類（包含大小分類）
    @GetMapping("/getProdCategory")
    public ApiResponse<List<ProductCategoryDTO>> findCategoryValues(){
        return ApiResponseUtils.success(lookupService.findCategoryValues());
    }

    // 根據傳入值取得分類
    @GetMapping("/getByLookupName")
    public ApiResponse<List<LookupDTO>> findByLookupName(String lookupName){
        return ApiResponseUtils.success(lookupService.findByLookupName(lookupName));
    }

}

