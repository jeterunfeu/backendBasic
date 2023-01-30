package com.srlab.basic.serverside.boards.services;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
import com.srlab.basic.serverside.queries.QueryBuilder;
import com.srlab.basic.serverside.utils.MapStructMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BoardService {

    private final Logger LOG = LoggerFactory.getLogger(BoardService.class);

    @Autowired
    private BoardRepository bRepository;

    public ResponseEntity<?> feeling(Long seq, String feeling) {
        try {
            Board origin = bRepository.findById(seq).orElseGet(null);
            Board target = new Board();

            if (feeling.equals("like")) {
                target.setLikeCount(origin.getLikeCount() + 1L);
            } else if (feeling.equals("dislike")) {
                target.setDislikeCount(origin.getDislikeCount() + 1L);
            }

            MapStructMapper.INSTANCE.update(target, origin);
            return new ResponseEntity<>(bRepository.save(origin), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> update(Long seq, Board data) {

        try {
            //origin
            Board origin = bRepository.findOneBySeq(seq);
            Board result = null;
            MapStructMapper.INSTANCE.update(data, origin);
            result = bRepository.save(origin);
            //update
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
