package com.srlab.basic.serverside.boards.services;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.repositories.CommentRepository;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.hierarchies.repositories.HierarchyDataRepository;
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


    public Reply save(Reply data) {
        try{
            return cRepository.save(data);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Reply findOne(Long seq) {
        try{
            return cRepository.findOneBySeq(seq).orElseGet(null);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<?> feeling(Long seq, String feeling) {
        try {
            Reply origin = cRepository.findOneBySeq(seq).orElseGet(null);
            Reply target = new Reply();

            if (feeling.equals("like")) {
                target.setLikeCount(origin.getLikeCount() + 1L);
            } else if (feeling.equals("dislike")) {
                target.setDislikeCount(origin.getDislikeCount() + 1L);
            }

            MapStructMapper.INSTANCE.update(target, origin);
            return new ResponseEntity<>(cRepository.save(origin), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Reply update(Reply ori, Reply tar) {
        MapStructMapper.INSTANCE.update(tar, ori);
        return cRepository.save(ori);
    }

    public ResponseEntity<?> update(Long seq, Reply data) {
        try{
            Reply origin = findOne(seq);
            MapStructMapper.INSTANCE.update(data, origin);

            return new ResponseEntity<>(cRepository.save(origin), HttpStatus.OK);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
