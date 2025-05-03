package io.github.jann1325.repository;

import io.github.jann1325.domain.LoginToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LoginTokenRepository extends JpaRepository<LoginToken, Integer> {

    Optional<LoginToken> findByTokenAndUsedIsFalse(String token);

    LoginToken findTopByEmailOrderByCreatedTimeDesc(String email);
}
