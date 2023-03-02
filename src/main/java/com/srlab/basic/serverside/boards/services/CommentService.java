package com.srlab.basic.serverside.boards.services;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
import com.srlab.basic.serverside.boards.repositories.CommentRepository;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.hierarchies.repositories.HierarchyDataRepository;
import com.srlab.basic.serverside.utils.MapStructMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CommentService {

    private final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentRepository cRepository;
    @Autowired
    private BoardRepository bRepository;


    public Reply save(Reply data) {
        try {
            Long bSeq = data.getBoard().getSeq();
            Board board = bRepository.findOneBySeq(bSeq).orElse(null);
            data.setBoard(board);

            Reply result = cRepository.save(data);
            cRepository.refresh(result);

            List<Reply> replies = board.getReplies();

            if (replies != null) {
                replies.add(result);

                board.setReplies(replies);

                Board bResult = bRepository.save(board);
                bRepository.refresh(bResult);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Reply findOne(Long seq) {
        try {
            return cRepository.findOneBySeq(seq).orElseGet(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<?> feeling(Long seq, String id, String feeling) {
        try {
            Reply origin = cRepository.findOneBySeq(seq).orElseGet(null);
            Reply target = new Reply();
            //origin에서 해당 아이디 있는지 확인합니다.
            //있으면 return하고 없으면 값을 넣습니다.
            String[] arr;
            String member1 = origin.getLikeMember();
            String member2 = origin.getDislikeMember();

            if (feeling.equals("like")) {
                arr = member1 == null ? null : member1.split(",");
                if(arr == null || Arrays.stream(arr).anyMatch(id::equals) == false){
                    if (arr == null) {
                        target.setLikeMember(id);
                    } else {
                        target.setLikeMember(member2 + "," + id);
                    }
                } else {
                    return new ResponseEntity<>("already Exists", HttpStatus.BAD_REQUEST);
                }
            } else if (feeling.equals("dislike")) {
                arr = member2 == null ? null : member2.split(",");
                if(arr == null || Arrays.stream(arr).anyMatch(id::equals) == false){
                    if (arr == null) {
                        target.setDislikeMember(id);
                    } else {
                        target.setDislikeMember(member2 + "," + id);
                    }
                } else {
                    return new ResponseEntity<>("already Exists", HttpStatus.BAD_REQUEST);
                }
            }

            MapStructMapper.INSTANCE.update(target, origin);
            return new ResponseEntity<>(cRepository.save(origin), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Reply update(Reply ori, Reply tar) {
        MapStructMapper.INSTANCE.update(tar, ori);
        Reply result = save(ori);
        return result;
    }

    public ResponseEntity<?> update(Long seq, Reply data) {
        try {
            Reply origin = findOne(seq);
            data.setSeq(seq);
            Reply result = update(origin, data);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
