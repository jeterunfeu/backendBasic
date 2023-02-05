package com.srlab.basic.serverside.files.services;

import com.srlab.basic.serverside.configs.YamlConfig;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.files.models.TempFile;
import com.srlab.basic.serverside.files.repositories.FileRepository;
import com.srlab.basic.serverside.files.repositories.FileTempRepository;
import com.srlab.basic.serverside.queries.QueryBuilder;
import com.srlab.basic.serverside.utils.FileUtil;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FileService {

    private final Logger LOG = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FileTempRepository fileTempRepository;
    @Autowired
    private QueryBuilder<TempFile> queryBuilderTemp;
    @Autowired
    private QueryBuilder<AvailableFile> queryBuilderAvailable;
    @Autowired
    private YamlConfig config;

    public ResponseEntity<?> download(String key) throws MalformedURLException {
        try {
            AvailableFile file = (AvailableFile) findByKey(key).getBody();
            Resource resource = (Resource) getDownResource(file, key).getBody();
            return new ResponseEntity<>(resource.exists() ? resource : null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getDownResource(AvailableFile file, String key) throws MalformedURLException {
        try {
            Path fileSourceLocation = Paths.get(file.getFilePath() + "/").toAbsolutePath().normalize();
            String fileName = file.getInsertedDate() + "_" + file.getFileName();
            Path filePath = fileSourceLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<?> findByKey(String key) {
        try {
            return new ResponseEntity<>(fileNullCheck(fileRepository.findByKey(key)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> fileNullCheck(AvailableFile file) {
        try {
            return new ResponseEntity<>(file == null ? null : file, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getFileTemporaryOne(String key) {
        try {
            return new ResponseEntity<>(fileTempRepository.findByKey(key), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> insert(String category, MultipartFile[] files) {
        try {
            List<TempFile> list = new ArrayList<>();

            String temporaryDirectory = config.getTemp();

            for (MultipartFile multipartFile : files) {
                String mimeType = multipartFile.getContentType();
                byte[] fileAsBytes = multipartFile.getBytes();

                String originFileName = multipartFile.getOriginalFilename();

                if (originFileName.contains(File.separator)) {
                    originFileName = FileUtil.getFileNameFromFileFullPath(originFileName);
                }

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy");
                String year = format.format(date);
                String path = temporaryDirectory + "/" + year;
                File f = new File(path);
                if (!f.exists()) {
                    f.mkdirs();
                }

                Path fileStorageTarget = Paths.get(path + "/").toAbsolutePath().normalize();

                UUID uuid = UUID.randomUUID();
                Long unixTime = System.currentTimeMillis();
                String uploadFileName = unixTime + "_" + uuid;

                TempFile fileTemp = TempFile.builder()
                        .category(category)
                        .key(uuid.toString())
                        .fileName(originFileName)
                        .fileExt(FilenameUtils.getExtension(originFileName))
                        .filePath(path + "/" + uploadFileName)
                        .fileMimeType(mimeType)
                        .insertedDate(new Date())
                        .fileSize(fileAsBytes.length)
                        .build();

                list.add(fileTemp);

                fileTempRepository.save(fileTemp);

                // file copy
                InputStream is = multipartFile.getInputStream();

                Path moveLocation = fileStorageTarget.resolve(uploadFileName); // real file
                Files.copy(is, moveLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> transfer(String category, Long seq, Map<String, String> map) {
        try {
            List<AvailableFile> files = new ArrayList<>();
            List<AvailableFile> result = null;
            String key;
            Optional<TempFile> tempFile = fileTempRepository.findById(seq);
            if (tempFile.isPresent()) {
                for (int i = 0; i < map.size(); i++) {
                    key = String.valueOf(map.get("file" + (i + 1)));
                    files.add((AvailableFile) transOneFile(category, seq, key).getBody());
                }
                result = files;
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

    private ResponseEntity<?> transOneFile(String category, Long seq, String key) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String year = format.format(new Date());

            TempFile fileTemp = (TempFile) getFileTemporaryOne(key).getBody();

            String fullPath = config.getPath() + "/" + year + "/" +
                    fileTemp.getInsertedDate() + "_" + fileTemp.getFileName();

            AvailableFile file = AvailableFile.builder()
                    .insertedDate(fileTemp.getInsertedDate())
                    .fileExt(fileTemp.getFileExt())
                    .filePath(fullPath)
                    .fileName(fileTemp.getFileName())
                    .fileSize(fileTemp.getFileSize())
                    .key(fileTemp.getKey())
                    .fileMimeType(fileTemp.getFileMimeType())
                    .category(category)
                    .build();

            file = fileRepository.save(file);

            // file copy process
            fileCopy(fileTemp, year);

            return new ResponseEntity<>(file, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> fileCopy(TempFile file, String year) throws Exception {

        try {
            String sourcePath = config.getTemp() + "/" + year;
            String path = config.getPath() + "/" + year;

            Path fileSource = Paths.get(path + "/").toAbsolutePath().normalize();
            Path fileTarget = Paths.get(sourcePath + "/").toAbsolutePath().normalize();
            String saveName = file.getInsertedDate() + "_" + file.getFileName();

            File f = new File(path);

            if (!f.exists()) f.mkdirs();

            Path sourceLocation = fileTarget.resolve(file.getFileName());
            Path targetLocation = fileSource.resolve(saveName);
            Files.copy(sourceLocation, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }

    }

    public ResponseEntity<?> deleteFileByKey(String key) {
        try {
            File chkfile = (File) checkExistsFile(key).getBody();
            Boolean result = false;
            if (chkfile != null && chkfile.exists()) {
                chkfile.delete();
                result = true;
            } else {
                System.out.println("Not Found The File");
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private ResponseEntity<?> checkExistsFile(String key) {
        try {
            AvailableFile file = fileRepository.findOneByKey(key);
            File result = null;
            if (file != null) {
                String fileFullPath = file.getFilePath();
                File chkfile = new File(fileFullPath);
                if (chkfile.exists()) {
                    result = chkfile;
                }
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}