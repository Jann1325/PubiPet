package io.github.jann1325.repository;

import io.github.jann1325.domain.Product;
import io.github.jann1325.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(nativeQuery = true, value =
            "SELECT p.id AS id, p.prod_name AS prodName, p.price AS price, p.discount_price AS discountPrice, p.inventory AS inventory, pi.image_url AS imageUrl " +
                    "FROM product p " +
                    "JOIN lookup l ON p.category_id = l.id " +
                    "LEFT JOIN (" +
                    "  SELECT pi.product_id, MIN(pi.sort_order) AS sort_order, MIN(pi.image_url) AS image_url" +
                    "  FROM product_image pi" +
                    "  GROUP BY pi.product_id" +
                    ") pi ON pi.product_id = p.id "+
                    "WHERE (:categoryId IS NULL OR p.category_id = :categoryId OR l.parent_id = :categoryId) " +
                    "AND (:keyword IS NULL OR p.prod_name LIKE CONCAT('%', :keyword, '%') " +
                    "OR p.prod_description LIKE CONCAT('%', :keyword, '%'))")
    Page<ProductDTO.ProductListDTO> findProducts(@Param("categoryId") Integer categoryId,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);

    @Query(nativeQuery = true, value =
            "SELECT p.id AS id, p.prod_name AS prodName, p.price AS price, p.discount_price AS discountPrice, p.inventory AS inventory, pi.image_url AS imageUrl " +
                    "FROM product p " +
                    "LEFT JOIN (" +
                    "  SELECT pi.product_id, MIN(pi.sort_order) AS sort_order, MIN(pi.image_url) AS image_url " +
                    "  FROM product_image pi " +
                    "  GROUP BY pi.product_id" +
                    ") pi ON pi.product_id = p.id " +
                    "WHERE p.id IN (:productIds)")
    List<ProductDTO.ProductListDTO> findProductsByIds(@Param("productIds") List<Integer> productIds);



}
