package com.srlab.basic.serverside.boards.services;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.files.repositories.FileRepository;
import com.srlab.basic.serverside.files.services.FileService;
import com.srlab.basic.serverside.utils.MapStructMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BoardService {

    private final Logger LOG = LoggerFactory.getLogger(BoardService.class);

    @Autowired
    private BoardRepository bRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FileService fileService;

    public ResponseEntity<?> feeling(Long seq, String id, String feeling) {
        try {
            Board origin = bRepository.findOneBySeq(seq).orElseGet(null);
            Board target = new Board();
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

            Board result = bRepository.save(origin);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Board save(Board data) {
        try {

            List<AvailableFile> files = data.getFiles();

            Board result = bRepository.save(data);

            if(files != null) {
                for (AvailableFile file: files) {
                    LOG.info("seq : " + file.getSeq());
                    AvailableFile origin = fileRepository.findOneBySeq(file.getSeq());
                    origin.setBoard(result);
                    fileRepository.save(origin);
                    fileRepository.refresh(origin);
                }
            }

            bRepository.refresh(result);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Board findOne(Long seq) {
        try {
            return bRepository.findOneBySeq(seq).orElseGet(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Board update(Board ori, Board tar) {
        MapStructMapper.INSTANCE.update(tar, ori);
        LOG.info("origin : " + ori.toString());
        LOG.info("target : " + tar.toString());
        Board result = save(ori);
        return result;
    }

    public ResponseEntity<?> update(Long seq, Board data) {
        try {
            Board origin = findOne(seq);
            data.setSeq(seq);
            Board result = update(origin, data);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
