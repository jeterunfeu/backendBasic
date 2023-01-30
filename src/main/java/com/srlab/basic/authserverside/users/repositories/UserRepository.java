package com.srlab.basic.authserverside.users.repositories;

import com.srlab.basic.authserverside.users.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo, Long> {

    @Query(" select ui from UserInfo ui where ui.id=:str ")
    UserInfo findOneById(@Param("str") String id);

    Optional<UserInfo> findByEmail(String email);
}
