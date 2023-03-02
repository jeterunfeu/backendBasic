package com.srlab.basic.serverside.boards.repositories;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.custom.CustomRepository;
import com.srlab.basic.serverside.custom.CustomRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;

import javax.transaction.Transactional;
import java.util.Optional;

public interface BoardRepository extends CustomRepository<Board, Long> {

    Optional<Board> findOneBySeq(Long seq);

}
