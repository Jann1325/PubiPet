package io.github.jann1325.repository;

import io.github.jann1325.domain.UserCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCartRepository extends JpaRepository<UserCart, Integer> {

    Optional<UserCart> findByUserId(Integer userId);

}
