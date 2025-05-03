package io.github.jann1325.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String prodName;

    private String prodDescription;

    private String features;

    private Integer categoryId;

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

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}