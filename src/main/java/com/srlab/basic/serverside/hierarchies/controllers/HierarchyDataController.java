package com.srlab.basic.serverside.hierarchies.controllers;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.services.BoardService;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.hierarchies.repositories.HierarchyDataRepository;
import com.srlab.basic.serverside.hierarchies.services.HierarchyDataService;
import com.srlab.basic.serverside.queries.CommonDataService;
import com.srlab.basic.serverside.queries.QueryBuilder;
import com.srlab.basic.serverside.utils.Helper;
import com.srlab.basic.serverside.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
@RequestMapping("/api/hierarchies")
public class HierarchyDataController {

    private final Logger LOG = LoggerFactory.getLogger(HierarchyDataController.class);
    @Autowired
    private HierarchyDataService hDataService;
    @Autowired
    private HierarchyDataRepository hRepository;
    @Autowired
    private CommonDataService<HierarchyData, HierarchyDataService, HierarchyDataRepository> commonDataService;
    @Autowired
    private Response response;

    public void HierarchySet() {
        try {
            commonDataService.set(HierarchyData.class, HierarchyDataService.class, hRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> hierarchyFindAll(HttpServletRequest req, @RequestParam Map<String, String> param, Pageable pageable/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        LOG.info("findAll");
        HierarchySet();
        return commonDataService.findAll("hierarchyData", param, pageable);
    }

    @GetMapping("/{seq}")
    public ResponseEntity<?> hierarchyFindOne(HttpServletRequest req, @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        HierarchySet();
        return commonDataService.findOne(seq);
    }

    //same level

    @PostMapping()
    public ResponseEntity<?> hierarchyInsert(HttpServletRequest req, @RequestBody HierarchyData data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        LOG.info("insert1");
        HierarchySet();
        return commonDataService.insert("hierarchyData", data);
    }

    @PostMapping("/{seq}")
    public ResponseEntity<?> hierarchyInsert(HttpServletRequest req, @PathVariable Long seq, @RequestBody HierarchyData data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        //root 는 반드시 존재해야하므로 기본 sql에 root 넣을것
        HierarchySet();
        return commonDataService.addSet("hierarchyData", seq, data, false);
    }

    //new level
    @PostMapping("/{seq}/depth")
    public ResponseEntity<?> hierarchyInsertDepth(HttpServletRequest req, @PathVariable Long seq, @RequestBody HierarchyData data, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        HierarchySet();
        return commonDataService.addSet("hierarchyData", seq, data, true);
    }

    @PutMapping("/{seq}")
    public ResponseEntity<?> hierarchyUpdate(HttpServletRequest req, @PathVariable Long seq, @RequestBody HierarchyData data, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return hDataService.update(seq, data);
    }

    @PutMapping("/{seq}/up")
    public ResponseEntity<?> hierarchyMoveUp(HttpServletRequest req, @PathVariable Long seq, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        HierarchySet();
        return commonDataService.moveUp("hierarchyData", seq);
    }

    @PutMapping("/{seq}/down")
    public ResponseEntity<?> hierarchyMoveDown(HttpServletRequest req, @PathVariable Long seq, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        HierarchySet();
        return commonDataService.moveDown("hierarchyData", seq);
    }

    @DeleteMapping("/{seq}")
    public ResponseEntity<?> hierarchyDelete(HttpServletRequest req, @PathVariable Long seq, @RequestBody HierarchyData data, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        HierarchySet();
        return commonDataService.subtractSet("hierarchyData", seq);
    }

}
