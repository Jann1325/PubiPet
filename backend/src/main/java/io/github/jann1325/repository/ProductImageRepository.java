package io.github.jann1325.repository;

import io.github.jann1325.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProductIdOrderBySortOrderAsc(Integer productId);

}
