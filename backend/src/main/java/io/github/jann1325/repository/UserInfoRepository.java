package io.github.jann1325.repository;

import io.github.jann1325.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    UserInfo findByEmail(String email);

}
