package com.srlab.basic.serverside.boards.controllers;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
import com.srlab.basic.serverside.boards.services.BoardService;
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

        return bService.feeling(seq, feeling);
    }

    //same level

    @Operation(description = "board insert", responses = { @ApiResponse(responseCode = "200", description = "inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping()
    public ResponseEntity<?> boardRootInsert(HttpServletRequest req, @RequestBody Board data) {

        BoardSet();
        return commonDataService.insert("board", data);
    }

    @Operation(description = "board same depth insert", responses = { @ApiResponse(responseCode = "200", description = "same depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/{seq}")
    public ResponseEntity<?> boardInsert(HttpServletRequest req, @PathVariable Long seq, @RequestBody Board data) {

        BoardSet();
        return commonDataService.addSet("board", seq, data, false);
    }

    //new level
    @Operation(description = "board sub depth insert", responses = { @ApiResponse(responseCode = "200", description = "sub depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/{seq}/depth")
    public ResponseEntity<?> boardInsertDepth(HttpServletRequest req, @PathVariable Long seq,
                                              @RequestBody Board data) {

        BoardSet();
        return commonDataService.addSet("board", seq, data, true);
    }

    @Operation(description = "board update", responses = { @ApiResponse(responseCode = "200", description = "updated"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping("/{seq}")
    public ResponseEntity<?> boardUpdate(HttpServletRequest req, @PathVariable Long seq,
                                         @RequestBody Board data) {

        return bService.update(seq, data);
    }

    @Operation(description = "board node move up", responses = { @ApiResponse(responseCode = "200", description = "moved up"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping("/{seq}/up")
    public ResponseEntity<?> boardMoveUp(HttpServletRequest req, @PathVariable Long seq) {

        BoardSet();
        return commonDataService.moveUp("board", seq);
    }

    @Operation(description = "board node move down", responses = { @ApiResponse(responseCode = "200", description = "moved down"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping("/{seq}/down")
    public ResponseEntity<?> boardMoveDown(HttpServletRequest req, @PathVariable Long seq) {

        BoardSet();
        return commonDataService.moveDown("board", seq);
    }

    @Operation(description = "board delete", responses = { @ApiResponse(responseCode = "200", description = "deleted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @DeleteMapping("/{seq}")
    public ResponseEntity<?> boardDelete(HttpServletRequest req, @PathVariable Long seq,
                                         @RequestBody HierarchyData data) {

        BoardSet();
        return commonDataService.subtractSet("board", seq);
    }
}
