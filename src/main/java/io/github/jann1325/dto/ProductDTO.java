package io.github.jann1325.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private ProductInfoDTO productInfoDTO;
    private ProductListDTO productListDTO;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductListDTO {
        private Integer id;
        private String prodName;
        private Integer price;
        private Integer discountPrice;
        private Integer inventory;
        private String imageUrl;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfoDTO {
        private Integer id;
        private String prodName;
        private String prodDescription;
        private String features;
        private Integer inventory;
        private Integer price;
        private Integer discountPrice;
        private String status;
        private String origin;
        private String material;
        private String suitableFor;
        private String shelfLife;
        private BigDecimal weight;
        private BigDecimal volume;
        private String size;
        private List<String> images;
    }

}
