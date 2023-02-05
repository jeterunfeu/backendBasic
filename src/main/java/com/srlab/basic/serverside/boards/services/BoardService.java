package com.srlab.basic.serverside.boards.services;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
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
public class BoardService {

    private final Logger LOG = LoggerFactory.getLogger(BoardService.class);

    @Autowired
    private BoardRepository bRepository;

    public ResponseEntity<?> feeling(Long seq, String feeling) {
        try {
            Board origin = bRepository.findOneBySeq(seq).orElseGet(null);
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

    public Board save(Board data) {
        try{
            return bRepository.save(data);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Board findOne(Long seq) {
        try{
            return bRepository.findOneBySeq(seq).orElseGet(null);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Board update(Board ori, Board tar) {
        MapStructMapper.INSTANCE.update(tar, ori);
        return bRepository.save(ori);
    }

    public ResponseEntity<?> update(Long seq, Board data) {
        try{
            Board origin = findOne(seq);
            MapStructMapper.INSTANCE.update(data, origin);

            return new ResponseEntity<>(bRepository.save(origin), HttpStatus.OK);
        }catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
