package com.srlab.basic.serverside.boards.controllers;

import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.repositories.CommentRepository;
import com.srlab.basic.serverside.boards.services.CommentService;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.queries.CommonDataService;
import com.srlab.basic.serverside.utils.Helper;
import com.srlab.basic.serverside.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final Logger LOG = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService cService;
    @Autowired
    private CommentRepository cRepository;
    @Autowired
    private Response response;

    @Autowired
    private CommonDataService<Reply, CommentService, CommentRepository> commonDataService;

    public void commentSet() {
        try {
            commonDataService.set(Reply.class, CommentService.class, cRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> commentFindAll(HttpServletRequest req, @RequestParam Map<String, String> param,/* Errors errors,*/ Pageable pageable) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        commentSet();
        return commonDataService.findAll("reply", param, pageable);
    }

    @GetMapping("/{seq}")
    public ResponseEntity<?> commentFindOne(HttpServletRequest req,  @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        commentSet();
        return commonDataService.findOne(seq);
    }

    @PostMapping()
    public ResponseEntity<?> commentInsert(HttpServletRequest req, @RequestBody Reply data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        commentSet();
        return commonDataService.insert("reply", data);
    }

    //same level
    @PostMapping("/{seq}")
    public ResponseEntity<?> commentInsert(HttpServletRequest req,  @PathVariable Long seq, @RequestBody Reply data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        commentSet();
        return commonDataService.addSet("reply", seq, data, false);
    }

    //new level
    @PostMapping("/{seq}/depth")
    public ResponseEntity<?> commentInsertDepth(HttpServletRequest req,  @PathVariable Long seq, @RequestBody Reply data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        commentSet();
        return commonDataService.addSet("reply", seq, data, true);
    }

    @PutMapping("/{seq}")
    public ResponseEntity<?> commentUpdate(HttpServletRequest req,  @PathVariable Long seq, @RequestBody Reply data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        return cService.update(seq, data);
    }

    @PutMapping("/{seq}/up")
    public ResponseEntity<?> commentMoveUp(HttpServletRequest req,  @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        commentSet();
        return commonDataService.moveUp("reply", seq);
    }

    @PutMapping("/{seq}/down")
    public ResponseEntity<?> commentMoveDown(/*Errors errors,*/HttpServletRequest req,  @PathVariable Long seq) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        commentSet();
        return commonDataService.moveDown("reply", seq);
    }

    @DeleteMapping("/{seq}")
    public ResponseEntity<?> commentDelete(/*Errors errors,*/HttpServletRequest req,  @PathVariable Long seq, @RequestBody HierarchyData data) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        commentSet();
        return commonDataService.subtractSet("reply", seq);
    }
}
