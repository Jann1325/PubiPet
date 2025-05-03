package io.github.jann1325.repository;

import io.github.jann1325.domain.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRecordRepository extends JpaRepository<OrderRecord, Integer> {

    List<OrderRecord> findByUserId(Integer userId);

}
