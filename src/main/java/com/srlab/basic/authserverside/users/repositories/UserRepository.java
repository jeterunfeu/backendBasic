package com.srlab.basic.authserverside.users.repositories;

import com.srlab.basic.authserverside.users.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo, Long> {

    @Query(" select u from UserInfo u where u.id = :id ")
    Optional<UserInfo> findOneById(@Param("id") String id);

}
