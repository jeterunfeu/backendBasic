package com.srlab.basic.serverside.boards.controllers;

import com.srlab.basic.authserverside.users.models.UserInfo;
import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
import com.srlab.basic.serverside.boards.repositories.CommentRepository;
import com.srlab.basic.serverside.boards.services.BoardService;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.files.repositories.FileRepository;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.queries.CommonDataService;
import com.srlab.basic.serverside.utils.UserInfoUtil;
import io.swagger.models.auth.In;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.srlab.basic.serverside.utils.IpUtil.getClientIp;

//@Tag(name = "board")
@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final Logger LOG = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService bService;
    @Autowired
    private BoardRepository bRepository;

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserInfoUtil userInfoUtil;

    @Autowired
    private CommonDataService<Board, BoardService, BoardRepository> commonDataService;

    Board board = new Board();
    public void BoardSet() {
        try {
            commonDataService.set(board, bService, bRepository);
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @Operation(description = "board find all", responses = { @ApiResponse(responseCode = "200", description = "find all"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping()
    public ResponseEntity<?> boardFindAll(HttpServletRequest req, @RequestParam Map<String, String> param,
                                          Pageable pageable) {

        BoardSet();
        return commonDataService.findAll("board", param, pageable);
    }

    @Operation(description = "board find one", responses = { @ApiResponse(responseCode = "200", description = "find one"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/{seq}")
    public ResponseEntity<?> boardFindOne(HttpServletRequest req, @PathVariable Long seq) {

        BoardSet();
        return commonDataService.findOne(seq);
    }

    @Operation(description = "board like/dislike", responses = { @ApiResponse(responseCode = "200", description = "like/dislike checked"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/{seq}/{feeling}")
    public ResponseEntity<?> feelingFindOne(HttpServletRequest req, @PathVariable("seq") Long seq,
                                            @PathVariable("feeling") String feeling) {

        UserInfo userInfo = userInfoUtil.getUserData(req);
        Long id = userInfo.getSeq();
        return bService.feeling(seq, String.valueOf(id), feeling);
    }

    //same level

    @Operation(description = "board insert", responses = { @ApiResponse(responseCode = "200", description = "inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> boardRootInsert(HttpServletRequest req, @RequestBody Board data) {

        BoardSet();
        UserInfo userInfo = userInfoUtil.getUserData(req);
        data.setInsertedStringId(userInfo.getId());
        data.setInsertedId(userInfo.getSeq());
        data.setInsertedIp(getClientIp(req));
        data.setInsertedDate(new Date());
        return commonDataService.insert("board", data);
    }

    @Operation(description = "board same depth insert", responses = { @ApiResponse(responseCode = "200", description = "same depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping(path="/{seq}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> boardInsert(HttpServletRequest req, @PathVariable Long seq, @RequestBody Board data) {

        try {
            BoardSet();
            UserInfo userInfo = userInfoUtil.getUserData(req);
            data.setInsertedStringId(userInfo.getId());
            data.setInsertedId(userInfo.getSeq());
            data.setInsertedIp(getClientIp(req));
            data.setInsertedDate(new Date());
            return new ResponseEntity<>((Board) commonDataService.addSet("board", seq, data, false), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //new level
    @Operation(description = "board sub depth insert", responses = { @ApiResponse(responseCode = "200", description = "sub depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping(path="/{seq}/depth", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> boardInsertDepth(HttpServletRequest req, @PathVariable Long seq,
                                              @RequestBody Board data) {

        try {
            BoardSet();
            UserInfo userInfo = userInfoUtil.getUserData(req);
            data.setInsertedStringId(userInfo.getId());
            data.setInsertedId(userInfo.getSeq());
            data.setInsertedIp(getClientIp(req));
            data.setInsertedDate(new Date());
            return new ResponseEntity<>((Board) commonDataService.addSet("board", seq, data, true), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(description = "board update", responses = { @ApiResponse(responseCode = "200", description = "updated"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping(path="/{seq}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> boardUpdate(HttpServletRequest req, @PathVariable Long seq,
                                         @RequestBody Board data) {

        UserInfo userInfo = userInfoUtil.getUserData(req);
//        data.setInsertedStringId(userInfo.getId());
        data.setUpdatedId(userInfo.getSeq());
        data.setUpdatedIp(getClientIp(req));
        data.setUpdatedDate(new Date());
        return bService.update(seq, data);
    }

    @Operation(description = "board node move up", responses = { @ApiResponse(responseCode = "200", description = "moved up"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping(path="/{seq}/up", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> boardMoveUp(HttpServletRequest req, @PathVariable Long seq) {

        BoardSet();
        return commonDataService.moveUp("board", seq);
    }

    @Operation(description = "board node move down", responses = { @ApiResponse(responseCode = "200", description = "moved down"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping(path="/{seq}/down", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> boardMoveDown(HttpServletRequest req, @PathVariable Long seq) {

        BoardSet();
        return commonDataService.moveDown("board", seq);
    }

    @Operation(description = "board delete", responses = { @ApiResponse(responseCode = "200", description = "deleted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @DeleteMapping("/{seq}")
    public ResponseEntity<?> boardDelete(HttpServletRequest req, @PathVariable Long seq) {

        BoardSet();
        Board board = bRepository.findOneBySeq(seq).orElse(null);
        List<AvailableFile> list = board.getFiles();
        List<Reply> replyList = board.getReplies();
//        미리 file 목록 지울것 !!!
        if(list != null && !list.isEmpty()) {
            for(AvailableFile a: list){
                fileRepository.deleteOneByKey(a.getKey());
            }
        }

        //미리 comment 목록 지울것 !!!
        if(replyList != null && !replyList.isEmpty()) {
            for(Reply a: replyList){
                commentRepository.deleteOneBySeq(a.getSeq());
            }
        }

        return commonDataService.subtractSet("board", seq);
    }
}
