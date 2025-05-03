package io.github.jann1325.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryDTO {
    private Integer id;
    private String lookupKey;
    private String lookupValue;
    private Integer parentId;
    private List<ProductCategoryDTO> subCategories;
}
