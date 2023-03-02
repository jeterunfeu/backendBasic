package com.srlab.basic.serverside.boards.controllers;

import com.srlab.basic.authserverside.users.models.UserInfo;
import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.repositories.CommentRepository;
import com.srlab.basic.serverside.boards.services.CommentService;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.queries.CommonDataService;
import com.srlab.basic.serverside.utils.UserInfoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

import static com.srlab.basic.serverside.utils.IpUtil.getClientIp;

//@Tag(name = "comment")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final Logger LOG = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService cService;
    @Autowired
    private CommentRepository cRepository;
    @Autowired
    private UserInfoUtil userInfoUtil;

    @Autowired
    private CommonDataService<Reply, CommentService, CommentRepository> commonDataService;

    Reply reply = new Reply();
    public void commentSet() {
        try {
            commonDataService.set(reply, cService, cRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @Operation(description = "comment find all", responses = { @ApiResponse(responseCode = "200", description = "find all"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping()
    public ResponseEntity<?> commentFindAll(HttpServletRequest req, @RequestParam Map<String, String> param,
                                            Pageable pageable) {
        commentSet();
        return commonDataService.findAll("reply", param, pageable);
    }

    @Operation(description = "comment find one", responses = { @ApiResponse(responseCode = "200", description = "find one"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/{seq}")
    public ResponseEntity<?> commentFindOne(HttpServletRequest req,  @PathVariable Long seq) {

        commentSet();
        return commonDataService.findOne(seq);
    }

    @Operation(description = "comment like/dislike", responses = { @ApiResponse(responseCode = "200", description = "like/dislike checked"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/{seq}/{feeling}")
    public ResponseEntity<?> feelingFindOne(HttpServletRequest req, @PathVariable("seq") Long seq,
                                            @PathVariable("feeling") String feeling) {

        UserInfo userInfo = userInfoUtil.getUserData(req);
        Long id = userInfo.getSeq();

        return cService.feeling(seq, String.valueOf(id), feeling);
    }

    @Operation(description = "comment insert", responses = { @ApiResponse(responseCode = "200", description = "inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping(path="", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> commentRootInsert(HttpServletRequest req, @RequestBody Reply data) {

        commentSet();
        UserInfo userInfo = userInfoUtil.getUserData(req);
        data.setInsertedStringId(userInfo.getId());
        data.setInsertedId(userInfo.getSeq());
        data.setInsertedIp(getClientIp(req));
        data.setInsertedDate(new Date());
        return commonDataService.insert("reply", data);
    }

    //same level
    @Operation(description = "comment same depth insert", responses = { @ApiResponse(responseCode = "200", description = "same depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping(path="/{seq}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> commentInsert(HttpServletRequest req,  @PathVariable Long seq, @RequestBody Reply data) {

        try{
            commentSet();
            UserInfo userInfo = userInfoUtil.getUserData(req);
            data.setInsertedStringId(userInfo.getId());
            data.setInsertedId(userInfo.getSeq());
            data.setInsertedIp(getClientIp(req));
            data.setInsertedDate(new Date());
            return new ResponseEntity<>((Reply)commonDataService.addSet("reply", seq, data, false), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //new level
    @Operation(description = "comment sub depth insert", responses = { @ApiResponse(responseCode = "200", description = "sub depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PostMapping(path="/{seq}/depth", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> commentInsertDepth(HttpServletRequest req,  @PathVariable Long seq,
                                                @RequestBody Reply data) {

        try {
            LOG.info("d15");
            commentSet();
            UserInfo userInfo = userInfoUtil.getUserData(req);
            data.setInsertedStringId(userInfo.getId());
            LOG.info("d16");
            data.setInsertedId(userInfo.getSeq());
            data.setInsertedIp(getClientIp(req));
            data.setInsertedDate(new Date());
            LOG.info("d17");
            return new ResponseEntity<>((Reply) commonDataService.addSet("reply", seq, data, true), HttpStatus.OK);
        } catch(Exception e) {
            LOG.info("d18");
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(description = "comment update", responses = { @ApiResponse(responseCode = "200", description = "updated"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PutMapping(path="/{seq}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> commentUpdate(HttpServletRequest req,  @PathVariable Long seq,
                                           @RequestBody Reply data) {

        UserInfo userInfo = userInfoUtil.getUserData(req);
        data.setUpdatedId(userInfo.getSeq());
//        data.setInsertedId(userInfo.getSeq());
        data.setUpdatedIp(getClientIp(req));
        data.setUpdatedDate(new Date());
        return cService.update(seq, data);
    }

    @Operation(description = "comment node move up", responses = { @ApiResponse(responseCode = "200", description = "moved up"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PutMapping(path="/{seq}/up", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> commentMoveUp(HttpServletRequest req,  @PathVariable Long seq) {

        commentSet();
        return commonDataService.moveUp("reply", seq);
    }

    @Operation(description = "comment node move down", responses = { @ApiResponse(responseCode = "200", description = "moved down"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PutMapping(path="/{seq}/down", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> commentMoveDown(HttpServletRequest req,  @PathVariable Long seq) {

        commentSet();
        return commonDataService.moveDown("reply", seq);
    }

    @Operation(description = "comment delete", responses = { @ApiResponse(responseCode = "200", description = "deleted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @DeleteMapping("/{seq}")
    public ResponseEntity<?> commentDelete(HttpServletRequest req,  @PathVariable Long seq) {

        commentSet();
        return commonDataService.subtractSet("reply", seq);
    }
}
