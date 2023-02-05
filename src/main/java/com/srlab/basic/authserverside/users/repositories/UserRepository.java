package com.srlab.basic.authserverside.users.repositories;

import com.srlab.basic.authserverside.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(" select u from User u where u.id = :id ")
    Optional<User> findOneById(@Param("id") String id);

}
