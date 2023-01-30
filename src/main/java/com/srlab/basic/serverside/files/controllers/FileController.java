package com.srlab.basic.serverside.files.controllers;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
import com.srlab.basic.serverside.boards.services.BoardService;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.files.models.TempFile;
import com.srlab.basic.serverside.files.repositories.FileRepository;
import com.srlab.basic.serverside.files.repositories.FileTempRepository;
import com.srlab.basic.serverside.files.services.FileService;
import com.srlab.basic.serverside.queries.CommonDataService;
import com.srlab.basic.serverside.utils.Helper;
import com.srlab.basic.serverside.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final Logger LOG = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;
    @Autowired
    private Response response;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FileTempRepository fileTempRepository;
    @Autowired
    private CommonDataService<AvailableFile, FileService, FileRepository> commonDataService;

    @Autowired
    private CommonDataService<TempFile, FileService, FileTempRepository> tempCommonDataService;

    public void FileSet() {
        try {
            commonDataService.set(AvailableFile.class, FileService.class, fileRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    public void FileTempSet() {
        try {
            tempCommonDataService.set(TempFile.class, FileService.class, fileTempRepository);
        } catch(Exception e) {
            LOG.info(e.getMessage());
        }
    }

    @GetMapping("/temp")
    public ResponseEntity<?> fileFindTempAll(HttpServletRequest req, @RequestParam Map<String, String> param, Pageable pageable/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        return tempCommonDataService.findAll("tempFile", param, pageable);
    }

    @GetMapping()
    public ResponseEntity<?> fileFindAll(HttpServletRequest req, @RequestParam Map<String, String> param, Pageable pageable/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        return commonDataService.findAll("availableFile", param, pageable);
    }

    @GetMapping("/keys/{key}")
    public ResponseEntity<?> downloadFileByPath(HttpServletRequest req, /*@PathVariable("category") String category,*/
                                                @PathVariable("key") String key, @RequestParam("route") String route/*, Errors errors*/) throws IOException {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        try {
            Resource resource = (Resource) fileService.download(key).getBody();
            String contentType = req.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            return route.equals("yes") ?
                    ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                            .body(resource) :
                    ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(resource.getFilename(), "UTF-8") + "\"")
                            .body(resource);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{category}/attach")
    public ResponseEntity<?> attach(HttpServletRequest req, @PathVariable String category, @RequestParam("file") MultipartFile[] files/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        Map<String, String> result = new HashMap<>();
        List<TempFile> fileTemp = (List<TempFile>) fileService.insert(category, files).getBody();
        for(TempFile file: fileTemp) {
            result.put(file.getFileName(), file.getKey());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{category}/{seq}")
    public ResponseEntity<?> fileSave(HttpServletRequest req, @PathVariable("category") String category,
                                      @PathVariable("seq") Long seq, @RequestBody Map<String, String> map/*, Errors errors*/) throws IOException {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        return fileService.transfer(category, seq, map);
    }

    @DeleteMapping("/keys/{key}")
    public ResponseEntity<?> deleteFileByKey(HttpServletRequest req, /*@PathVariable("category") String category,*/
                                             @PathVariable("key") String key/*, Errors errors*/) {
        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
        return fileService.deleteFileByKey(key);
    }
}
