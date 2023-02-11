package com.srlab.basic.serverside.devices.controllers;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.devices.models.Device;
import com.srlab.basic.serverside.devices.repositories.DeviceRepository;
import com.srlab.basic.serverside.devices.services.DeviceService;
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

//@Tag(name = "device")
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final Logger LOG = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService dService;
    @Autowired
    private DeviceRepository dRepository;
    @Autowired
    private CommonDataService<Device, DeviceService, DeviceRepository> commonDataService;

    Device device = new Device();
    public void DeviceSet() {
        try {
            commonDataService.set(device, dService, dRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @Operation(description = "device find all", responses = { @ApiResponse(responseCode = "200", description = "find all"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @GetMapping()
    public ResponseEntity<?> deviceFindAll(HttpServletRequest req, @RequestParam Map<String, String> param,
                                           Pageable pageable) {
        DeviceSet();
        return commonDataService.findAll("device", param, pageable);
    }

    @Operation(description = "device find one", responses = { @ApiResponse(responseCode = "200", description = "find one"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @GetMapping("/{seq}")
    public ResponseEntity<?> deviceFindOne(HttpServletRequest req, @PathVariable Long seq) {
        DeviceSet();
        return commonDataService.findOne(seq);
    }

    //same level

    @Operation(description = "device insert", responses = { @ApiResponse(responseCode = "200", description = "inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping()
    public ResponseEntity<?> deviceRootInsert(HttpServletRequest req, @RequestBody Device data) {

        DeviceSet();
        return commonDataService.insert("device", data);
    }
    @Operation(description = "device same depth insert", responses = { @ApiResponse(responseCode = "200", description = "same depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/{seq}")
    public ResponseEntity<?> deviceInsert(HttpServletRequest req, @PathVariable Long seq, @RequestBody Device data) {
        DeviceSet();
        return commonDataService.addSet("device", seq, data, false);
    }

    //new level
    @Operation(description = "device sub depth insert", responses = { @ApiResponse(responseCode = "200", description = "sub depth inserted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/{seq}/depth")
    public ResponseEntity<?> deviceInsertDepth(HttpServletRequest req, @PathVariable Long seq,
                                               @RequestBody Device data) {
        DeviceSet();
        return commonDataService.addSet("device", seq, data, true);
    }

    @Operation(description = "device update", responses = { @ApiResponse(responseCode = "200", description = "updated"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping("/{seq}")
    public ResponseEntity<?> deviceUpdate(HttpServletRequest req, @PathVariable Long seq, @RequestBody Device data) {

        return dService.update(seq, data);
    }

    @Operation(description = "device node move up", responses = { @ApiResponse(responseCode = "200", description = "moved up"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping("/{seq}/up")
    public ResponseEntity<?> moveUp(HttpServletRequest req, @PathVariable Long seq) {

        DeviceSet();
        return commonDataService.moveUp("device", seq);
    }

    @Operation(description = "device node move down", responses = { @ApiResponse(responseCode = "200", description = "moved down"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PutMapping("/{seq}/down")
    public ResponseEntity<?> moveDown(HttpServletRequest req, @PathVariable Long seq) {

        DeviceSet();
        return commonDataService.moveDown("device", seq);
    }

    @Operation(description = "device delete", responses = { @ApiResponse(responseCode = "200", description = "deleted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @DeleteMapping("/{seq}")
    public ResponseEntity<?> delete(HttpServletRequest req, @PathVariable Long seq, @RequestBody Device data) {

        DeviceSet();
        return commonDataService.subtractSet("device", seq);
    }
}
