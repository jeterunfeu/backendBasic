package com.srlab.basic.serverside.boards.repositories;

import com.srlab.basic.serverside.boards.models.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface CommentRepository extends JpaRepository<Reply, Long> {

    Reply findOneBySeq(Long seq);
}
