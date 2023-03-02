package com.srlab.basic.serverside.boards.repositories;

import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.custom.CustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CommentRepository extends CustomRepository<Reply, Long> {

    Optional<Reply> findOneBySeq(Long seq);

    @Transactional
    @Modifying
    void deleteOneBySeq(Long seq);
}
