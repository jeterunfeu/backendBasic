package com.srlab.basic.serverside.boards.controllers;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
import com.srlab.basic.serverside.boards.services.BoardService;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.queries.CommonDataService;
import com.srlab.basic.serverside.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final Logger LOG = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService bService;
    @Autowired
    private BoardRepository bRepository;
    @Autowired
    private Response response;
    @Autowired
    private CommonDataService<Board, BoardService, BoardRepository> commonDataService;

    public void BoardSet() {
        try {
            commonDataService.set(Board.class, BoardService.class, bRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> boardFindAll(HttpServletRequest req, @RequestParam Map<String, String> param, Pageable pageable/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        BoardSet();
        return commonDataService.findAll( "board", param, pageable);
    }

    @GetMapping("/{seq}")
    public ResponseEntity<?> boardFindOne(HttpServletRequest req, @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        BoardSet();
        return commonDataService.findOne(seq);
    }

    @GetMapping("/{seq}/{feeling}")
    public ResponseEntity<?> feelingFindOne(HttpServletRequest req, @PathVariable("seq") Long seq, @PathVariable("feeling") String feeling/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        return bService.feeling(seq, feeling);
    }

    //same level
    @PostMapping("/{seq}")
    public ResponseEntity<?> boardInsert(HttpServletRequest req, @PathVariable Long seq, @RequestBody Board data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        BoardSet();
        return commonDataService.addSet("board", seq, data, false);
    }

    //new level
    @PostMapping("/{seq}/depth")
    public ResponseEntity<?> boardInsertDepth(HttpServletRequest req, @PathVariable Long seq, @RequestBody Board data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        BoardSet();
        return commonDataService.addSet("board", seq, data, true);
    }

    @PutMapping("/{seq}")
    public ResponseEntity<?> boardUpdate(HttpServletRequest req, @PathVariable Long seq, @RequestBody Board data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        return bService.update(seq, data);
    }

    @PutMapping("/{seq}/up")
    public ResponseEntity<?> boardMoveUp(HttpServletRequest req, @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        BoardSet();
        return commonDataService.moveUp("board", seq);
    }

    @PutMapping("/{seq}/down")
    public ResponseEntity<?> boardMoveDown(HttpServletRequest req, @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        BoardSet();
        return commonDataService.moveDown("board", seq);
    }

    @DeleteMapping("/{seq}")
    public ResponseEntity<?> boardDelete(HttpServletRequest req, @PathVariable Long seq, @RequestBody HierarchyData data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        BoardSet();
        return commonDataService.subtractSet("board", seq);
    }


}
