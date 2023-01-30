package com.srlab.basic.serverside.devices.controllers;

import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.services.CommentService;
import com.srlab.basic.serverside.devices.models.Device;
import com.srlab.basic.serverside.devices.repositories.DeviceRepository;
import com.srlab.basic.serverside.devices.services.DeviceService;
import com.srlab.basic.serverside.queries.CommonDataService;
import com.srlab.basic.serverside.utils.Helper;
import com.srlab.basic.serverside.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final Logger LOG = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService dService;
    @Autowired
    private Response response;
    @Autowired
    private DeviceRepository dRepository;
    @Autowired
    private CommonDataService<Device, DeviceService, DeviceRepository> commonDataService;

    public void DeviceSet() {
        try {
            commonDataService.set(Device.class, DeviceService.class, dRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> deviceFindAll(HttpServletRequest req, @RequestParam Map<String, String> param, Pageable pageable/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        DeviceSet();
        return commonDataService.findAll("device", param, pageable);
    }

    @GetMapping("/{seq}")
    public ResponseEntity<?> deviceFindOne(HttpServletRequest req, @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        DeviceSet();
        return commonDataService.findOne(seq);
    }

    //same level
    @PostMapping("/{seq}")
    public ResponseEntity<?> deviceInsert(HttpServletRequest req, @PathVariable Long seq, @RequestBody Device data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        DeviceSet();
        return commonDataService.addSet("device", seq, data, false);
    }

    //new level
    @PostMapping("/{seq}/depth")
    public ResponseEntity<?> deviceInsertDepth(HttpServletRequest req, @PathVariable Long seq, @RequestBody Device data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        DeviceSet();
        return commonDataService.addSet("device", seq, data, true);
    }

    @PutMapping("/{seq}")
    public ResponseEntity<?> deviceUpdate(HttpServletRequest req, @PathVariable Long seq, @RequestBody Device data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        return dService.update(seq, data);
    }

    @PutMapping("/{seq}/up")
    public ResponseEntity<?> moveUp(HttpServletRequest req, @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        DeviceSet();
        return commonDataService.moveUp("device", seq);
    }

    @PutMapping("/{seq}/down")
    public ResponseEntity<?> moveDown(HttpServletRequest req, @PathVariable Long seq/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        DeviceSet();
        return commonDataService.moveDown("device", seq);
    }

    @DeleteMapping("/{seq}")
    public ResponseEntity<?> delete(HttpServletRequest req, @PathVariable Long seq, @RequestBody Device data/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        DeviceSet();
        return commonDataService.subtractSet("device", seq);
    }
}
