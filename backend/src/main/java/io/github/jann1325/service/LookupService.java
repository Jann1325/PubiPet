package io.github.jann1325.service;

import io.github.jann1325.dto.LookupDTO;
import io.github.jann1325.dto.ProductCategoryDTO;
import io.github.jann1325.repository.LookupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class LookupService {
    @Autowired
    private LookupRepository lookupRepository;

    // 取得商品分類
    public List<ProductCategoryDTO> findCategoryValues(){
        // 獲取所有分類
        List<ProductCategoryDTO> categories = lookupRepository.findCategoryValues();
        // 使用一個 Map 將大分類與子分類進行對應
        Map<Integer, ProductCategoryDTO> categoryMap = new HashMap<>();
        for (ProductCategoryDTO category : categories) {
            categoryMap.put(category.getId().intValue(), category);
        }
        // 建立樹狀結構
        List<ProductCategoryDTO> rootCategories = new ArrayList<>();
        // 遍歷分類，將子分類添加到對應的大分類中
        for (ProductCategoryDTO category : categories) {
            if (category.getParentId() == null || category.getParentId() == 0) {
                // 大分類（parentId 為 null）
                rootCategories.add(category);
            } else {
                // 子分類（根據 parentId 找到對應的大分類）
                ProductCategoryDTO parentCategory = categoryMap.get(category.getParentId());
                if (parentCategory != null) {
                    if (parentCategory.getSubCategories() == null) {
                        parentCategory.setSubCategories(new ArrayList<>());
                    }
                    parentCategory.getSubCategories().add(category);
                }
            }
        }
        return rootCategories;
    }

    // 查詢其他一般分類
    public List<LookupDTO> findByLookupName(String lookupName){
        return lookupRepository.findByLookupName(lookupName);
    }



}
