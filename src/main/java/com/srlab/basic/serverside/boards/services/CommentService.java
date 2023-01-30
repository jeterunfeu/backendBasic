package com.srlab.basic.serverside.boards.services;

import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.repositories.CommentRepository;
import com.srlab.basic.serverside.utils.MapStructMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentRepository cRepository;

    public ResponseEntity<?> findOne(Long seq) {
        try {
            return new ResponseEntity<>(cRepository.findById(seq).orElseGet(null), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> update(Long seq, Reply data) {

        try{
            Reply origin = (Reply) findOne(seq).getBody();
            MapStructMapper.INSTANCE.update(data, origin);

            //update
            return new ResponseEntity<>(cRepository.save(origin), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
