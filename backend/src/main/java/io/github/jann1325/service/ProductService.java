package io.github.jann1325.service;

import io.github.jann1325.repository.ProductImageRepository;
import io.github.jann1325.repository.ProductRepository;
import io.github.jann1325.dto.ProductDTO;
import io.github.jann1325.domain.ProductImage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    // 商品列表頁
    public Map<String, Object> findProducts(Integer categoryId, String keyword, Integer page, Integer size, String sortBy) {
        Pageable pageable = PageRequest.of(Math.toIntExact(page - 1), Math.toIntExact(size),
                Sort.by(Sort.Order.asc(sortBy.equals("name") ? "prod_name" : "price")));
        Page<ProductDTO.ProductListDTO> resultPage = productRepository.findProducts(categoryId,keyword, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("products", resultPage.getContent());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("totalElements", resultPage.getTotalElements());
        response.put("currentPage", resultPage.getNumber() + 1);
        return response;
    }

    // 商品詳細頁面
    public ProductDTO.ProductInfoDTO getProductInfo(Integer productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    ProductDTO.ProductInfoDTO productInfoDTO = new ProductDTO.ProductInfoDTO();
                    BeanUtils.copyProperties(product, productInfoDTO);
                    // 取出圖片
                    List<String> imageUrls = productImageRepository
                            .findByProductIdOrderBySortOrderAsc(productId)
                            .stream()
                            .map(ProductImage::getImageUrl)
                            .collect(Collectors.toList());

                    productInfoDTO.setImages(imageUrls);
                    return productInfoDTO;
                })
                .orElse(null);
    }

    // 根據商品id查找購物車商品列表
    public List<ProductDTO.ProductListDTO> findProductsByIds(List<Integer> productIds) {
        return productRepository.findProductsByIds(productIds);
    }
}
