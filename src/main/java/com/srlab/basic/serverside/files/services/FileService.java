package com.srlab.basic.serverside.files.services;

import com.srlab.basic.serverside.boards.models.Board;
import com.srlab.basic.serverside.boards.models.Reply;
import com.srlab.basic.serverside.boards.repositories.BoardRepository;
import com.srlab.basic.serverside.configs.YamlConfig;
import com.srlab.basic.serverside.devices.models.Device;
import com.srlab.basic.serverside.files.models.AvailableFile;
import com.srlab.basic.serverside.files.models.TempFile;
import com.srlab.basic.serverside.files.repositories.FileRepository;
import com.srlab.basic.serverside.files.repositories.FileTempRepository;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.queries.QueryBuilder;
import com.srlab.basic.serverside.utils.FileUtil;
import com.srlab.basic.serverside.utils.MapStructMapper;
import net.bytebuddy.build.Plugin;
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
    private BoardRepository boardRepository;
    @Autowired
    private FileTempRepository fileTempRepository;
    @Autowired
    private QueryBuilder<TempFile> queryBuilderTemp;
    @Autowired
    private QueryBuilder<AvailableFile> queryBuilderAvailable;
    @Autowired
    private YamlConfig config;

    public Resource download(String key) throws MalformedURLException {
        try {
            AvailableFile file = findByKey(key);
            Resource resource = getDownResource(file, key);
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Resource getDownResource(AvailableFile file, String key) throws MalformedURLException {
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String newTime = simpleDateFormat.format(file.getInsertedDate());

            String originName = file.getFilePath().split("/")[4];

            Path fileSourceLocation = Paths.get(file.getFilePath() + "/").toAbsolutePath().normalize();
//            String fileName = newTime + "_" + originName+ "." + file.getFileExt();
//            Path filePath = fileSourceLocation.resolve(fileName).normalize();
            Path filePath = fileSourceLocation.normalize();
            Resource resource = new UrlResource(filePath.toUri());
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AvailableFile findByKey(String key) {
        try {
            return fileNullCheck(fileRepository.findByKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AvailableFile fileNullCheck(AvailableFile file) {
        try {
            return file == null ? null : file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

                is.close();
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> transfer(String category, Map<String, String> map) {
        try {
            List<AvailableFile> files = new ArrayList<>();
            List<AvailableFile> result = null;
            Optional<TempFile> tempFile;

            for (Map.Entry<String, String> pair : map.entrySet()) {
                LOG.info("file input");
                tempFile = fileTempRepository.findOneByKey(pair.getValue());
                if(tempFile.isPresent()) {
                    files.add(transOneFile(category, pair.getValue()/*, mKey*/));
                }
            }

                result = files;
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

    private AvailableFile transOneFile(String category, String key/*, String mKey*/) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            String year = format.format(new Date());

            TempFile fileTemp = (TempFile) getFileTemporaryOne(key).getBody();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String newTime = simpleDateFormat.format(fileTemp.getInsertedDate());

            String originName = fileTemp.getFilePath().split("/")[4];

            String fullPath = config.getPath() + "/" + year + "/" +
                    newTime + "_" + originName + "." + fileTemp.getFileExt();

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

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<?> fileCopy(TempFile file, String year) throws Exception {

        try {
            String sourcePath = config.getTemp() + "/" + year;
            String path = config.getPath() + "/" + year;

            Path fileSource = Paths.get(path + "/").toAbsolutePath().normalize();
            Path fileTarget = Paths.get(sourcePath + "/").toAbsolutePath().normalize();

            String originName = file.getFilePath().split("/")[4];

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String newTime = simpleDateFormat.format(file.getInsertedDate());

            String saveName = newTime + "_" + originName + '.' + file.getFileExt();

            File f = new File(path);

            if (!f.exists()) f.mkdirs();
            Path sourceLocation = fileTarget.resolve(originName);
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
                fileRepository.deleteOneByKey(key);
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

    public ResponseEntity<?> updateBySeq(Long seq, AvailableFile file) {
        try {
            AvailableFile ori = fileRepository.findOneBySeq(seq);
            file.setSeq(seq);
            MapStructMapper.INSTANCE.update(file, ori);
            AvailableFile result = fileRepository.save(ori);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> insertFile(AvailableFile file) {
        try {
            AvailableFile result = fileRepository.save(file);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}