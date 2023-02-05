package com.srlab.basic.serverside.hierarchies.controllers;

import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.hierarchies.repositories.HierarchyDataRepository;
import com.srlab.basic.serverside.hierarchies.services.HierarchyDataService;
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

//@Tag(name = "hierarchyData")
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

    public void HierarchySet() {
        try {
            commonDataService.set(HierarchyData.class, hDataService, hRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @Operation(description = "hierarchy find all", responses = { @ApiResponse(responseCode = "200", description = "find all"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping()
    public ResponseEntity<?> hierarchyFindAll(HttpServletRequest req, @RequestParam Map<String, String> param,
                                              Pageable pageable) {
        HierarchySet();
        return commonDataService.findAll("hierarchyData", param, pageable);
    }

    @Operation(description = "hierarchy find one", responses = { @ApiResponse(responseCode = "200", description = "find one"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/{seq}")
    public ResponseEntity<?> hierarchyFindOne(HttpServletRequest req, @PathVariable Long seq) {
        HierarchySet();
        return commonDataService.findOne(seq);
    }

    //same level
    @Operation(description = "hierarchy insert", responses = { @ApiResponse(responseCode = "200", description = "inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping()
    public ResponseEntity<?> hierarchyRootInsert(HttpServletRequest req, @RequestBody HierarchyData data) {
        HierarchySet();
        return commonDataService.insert("hierarchyData", data);
    }

    @Operation(description = "hierarchy same depth insert", responses = { @ApiResponse(responseCode = "200", description = "same depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/{seq}")
    public ResponseEntity<?> hierarchyInsert(HttpServletRequest req, @PathVariable Long seq,
                                             @RequestBody HierarchyData data) {
        HierarchySet();
        return commonDataService.addSet("hierarchyData", seq, data, false);
    }

    //new level
    @Operation(description = "hierarchy sub depth insert", responses = { @ApiResponse(responseCode = "200", description = "sub depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/{seq}/depth")
    public ResponseEntity<?> hierarchyInsertDepth(HttpServletRequest req, @PathVariable Long seq,
                                                  @RequestBody HierarchyData data) {
        HierarchySet();
        return commonDataService.addSet("hierarchyData", seq, data, true);
    }

    @Operation(description = "hierarchy update", responses = { @ApiResponse(responseCode = "200", description = "updated"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping("/{seq}")
    public ResponseEntity<?> hierarchyUpdate(HttpServletRequest req, @PathVariable Long seq,
                                             @RequestBody HierarchyData data) {
        return hDataService.update(seq, data);
    }

    @Operation(description = "hierarchy node move up", responses = { @ApiResponse(responseCode = "200", description = "moved up"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/{seq}/up")
    public ResponseEntity<?> hierarchyMoveUp(HttpServletRequest req, @PathVariable Long seq) {
        HierarchySet();
        return commonDataService.moveUp("hierarchyData", seq);
    }

    @Operation(description = "hierarchy node move down", responses = { @ApiResponse(responseCode = "200", description = "moved down"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/{seq}/down")
    public ResponseEntity<?> hierarchyMoveDown(HttpServletRequest req, @PathVariable Long seq) {
        HierarchySet();
        return commonDataService.moveDown("hierarchyData", seq);
    }

    @Operation(description = "hierarchy delete", responses = { @ApiResponse(responseCode = "200", description = "deleted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @DeleteMapping("/{seq}")
    public ResponseEntity<?> hierarchyDelete(HttpServletRequest req, @PathVariable Long seq) {
        HierarchySet();
        return commonDataService.subtractSet("hierarchyData", seq);
    }

}
