package io.github.jann1325.repository;

import io.github.jann1325.domain.Lookup;
import io.github.jann1325.dto.LookupDTO;
import io.github.jann1325.dto.ProductCategoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LookupRepository extends JpaRepository<Lookup, Integer> {

    @Query("SELECT new io.github.jann1325.dto.ProductCategoryDTO(l.id, l.lookupKey, l.lookupValue , l.parentId, NULL) " +
            "FROM Lookup l WHERE l.lookupName = 'product_category'")
    List<ProductCategoryDTO> findCategoryValues();

    @Query("SELECT new io.github.jann1325.dto.LookupDTO(l.lookupKey, l.lookupValue) " +
            "FROM Lookup l WHERE l.lookupName = :lookupName")
    List<LookupDTO> findByLookupName(String lookupName);



}
