package com.srlab.basic.serverside.boards.controllers;

import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.repositories.CommentRepository;
import com.srlab.basic.serverside.boards.services.CommentService;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.queries.CommonDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

        return cService.feeling(seq, feeling);
    }

    @Operation(description = "comment insert", responses = { @ApiResponse(responseCode = "200", description = "inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping()
    public ResponseEntity<?> commentRootInsert(HttpServletRequest req, @RequestBody Reply data) {

        commentSet();
        return commonDataService.insert("reply", data);
    }

    //same level
    @Operation(description = "comment same depth insert", responses = { @ApiResponse(responseCode = "200", description = "same depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/{seq}")
    public ResponseEntity<?> commentInsert(HttpServletRequest req,  @PathVariable Long seq, @RequestBody Reply data) {

        commentSet();
        return commonDataService.addSet("reply", seq, data, false);
    }

    //new level
    @Operation(description = "comment sub depth insert", responses = { @ApiResponse(responseCode = "200", description = "sub depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PostMapping("/{seq}/depth")
    public ResponseEntity<?> commentInsertDepth(HttpServletRequest req,  @PathVariable Long seq,
                                                @RequestBody Reply data) {

        commentSet();
        return commonDataService.addSet("reply", seq, data, true);
    }

    @Operation(description = "comment update", responses = { @ApiResponse(responseCode = "200", description = "updated"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PutMapping("/{seq}")
    public ResponseEntity<?> commentUpdate(HttpServletRequest req,  @PathVariable Long seq,
                                           @RequestBody Reply data) {

        return cService.update(seq, data);
    }

    @Operation(description = "comment node move up", responses = { @ApiResponse(responseCode = "200", description = "moved up"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PutMapping("/{seq}/up")
    public ResponseEntity<?> commentMoveUp(HttpServletRequest req,  @PathVariable Long seq) {

        commentSet();
        return commonDataService.moveUp("reply", seq);
    }

    @Operation(description = "comment node move down", responses = { @ApiResponse(responseCode = "200", description = "moved down"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PutMapping("/{seq}/down")
    public ResponseEntity<?> commentMoveDown(HttpServletRequest req,  @PathVariable Long seq) {

        commentSet();
        return commonDataService.moveDown("reply", seq);
    }

    @Operation(description = "comment delete", responses = { @ApiResponse(responseCode = "200", description = "deleted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @DeleteMapping("/{seq}")
    public ResponseEntity<?> commentDelete(HttpServletRequest req,  @PathVariable Long seq,
                                           @RequestBody HierarchyData data) {

        commentSet();
        return commonDataService.subtractSet("reply", seq);
    }
}
