package com.srlab.basic.serverside.files.controllers;

import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.files.models.TempFile;
import com.srlab.basic.serverside.files.repositories.FileRepository;
import com.srlab.basic.serverside.files.repositories.FileTempRepository;
import com.srlab.basic.serverside.files.services.FileService;
import com.srlab.basic.serverside.queries.CommonDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Tag(name = "file")
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final Logger LOG = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FileTempRepository fileTempRepository;
    @Autowired
    private CommonDataService<AvailableFile, FileService, FileRepository> commonDataService;

    @Autowired
    private CommonDataService<TempFile, FileService, FileTempRepository> tempCommonDataService;

    AvailableFile availableFile = new AvailableFile();
    TempFile tempFile = new TempFile();
    public void FileSet() {
        try {
            commonDataService.set(availableFile, fileService, fileRepository);
        } catch(Exception e) {
            e.printStackTrace();
            LOG.info(e.getMessage());
        }
    }

    public void FileTempSet() {
        try {
            tempCommonDataService.set(tempFile, fileService, fileTempRepository);
        } catch(Exception e) {
            e.printStackTrace();
            LOG.info(e.getMessage());
        }
    }

    @Operation(description = "temp file find all", responses = { @ApiResponse(responseCode = "200", description = "find all"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/temp")
    public ResponseEntity<?> fileFindTempAll(HttpServletRequest req, @RequestParam Map<String, String> param,
                                             Pageable pageable) {
        return tempCommonDataService.findAll("tempFile", param, pageable);
    }

    @Operation(description = "available file find all", responses = { @ApiResponse(responseCode = "200", description = "find all"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping()
    public ResponseEntity<?> fileFindAll(HttpServletRequest req, @RequestParam Map<String, String> param,
                                         Pageable pageable) {
        return commonDataService.findAll("availableFile", param, pageable);
    }

    @Operation(description = "download", responses = { @ApiResponse(responseCode = "200", description = "download"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @GetMapping("/keys/{key}")
    public ResponseEntity<?> downloadFileByPath(HttpServletRequest req, @PathVariable("key") String key,
                                                @RequestParam("route") String route) {
        try {
            Resource resource = (Resource) fileService.download(key).getBody();
            String contentType = req.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            return route.equals("yes") ?
                    ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "inline; filename=\"" + resource.getFilename() + "\"")
                            .body(resource) :
                    ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" +
                                            URLEncoder.encode(resource.getFilename(), "UTF-8") + "\"")
                            .body(resource);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(description = "file attach", responses = { @ApiResponse(responseCode = "200", description = "attached"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @PostMapping("/{category}/attach")
    public ResponseEntity<?> attach(HttpServletRequest req, @PathVariable String category,
                                    @RequestParam("file") MultipartFile[] files) {
        Map<String, String> result = new HashMap<>();
        List<TempFile> fileTemp = (List<TempFile>) fileService.insert(category, files).getBody();
        for(TempFile file: fileTemp) {
            result.put(file.getFileName(), file.getKey());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(description = "transport file", responses = { @ApiResponse(responseCode = "200", description = "transported"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})
    @PostMapping("/{category}/{seq}")
    public ResponseEntity<?> fileSave(HttpServletRequest req, @PathVariable("category") String category,
                                      @PathVariable("seq") Long seq, @RequestBody Map<String, String> map) {
        return fileService.transfer(category, seq, map);
    }

    @Operation(description = "file delete", responses = { @ApiResponse(responseCode = "200", description = "deleted"),
            @ApiResponse(responseCode = "400", description = "bad request"),
            @ApiResponse(responseCode = "500", description = "internal server error")})

    @DeleteMapping("/keys/{key}")
    public ResponseEntity<?> deleteFileByKey(HttpServletRequest req, @PathVariable("key") String key) {

        return fileService.deleteFileByKey(key);
    }
}
