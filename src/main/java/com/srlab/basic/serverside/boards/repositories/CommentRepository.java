package com.srlab.basic.serverside.boards.repositories;

import com.srlab.basic.serverside.boards.models.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Reply, Long> {

    Optional<Reply> findOneBySeq(Long seq);
}
