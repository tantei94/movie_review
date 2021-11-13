package org.gibaek.movie_review.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.gibaek.movie_review.dto.UploadResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@Log4j2
public class UploadController {

    @Value("${org.gibaek.upload.path}")
    private String UPLOAD_PATH;

    @PostMapping ("/uploadAjax")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] file) {
        List<UploadResultDTO> resultDTOList = new ArrayList<>();
        for (MultipartFile multipartFile : file) {
            if (!multipartFile.getContentType().startsWith("image")) {
                log.warn("this file is not image type");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            //실제 파일 이름 EI나 Edge 는 전체 경로가 들어오므로
            String originalFileName = multipartFile.getOriginalFilename();
            log.info("originalFileName: {}", originalFileName);
            String fileName = originalFileName.substring(originalFileName.lastIndexOf("\\") +1);
            log.info("fileName: {}",fileName);

            //날짜 폴더 생성
            String folderPath = makeFolderPath();

            //uuid
            String uuid = java.util.UUID.randomUUID().toString();

            //저장할 파일 이름 중간에 _를 넣어 구분
            String saveFileName = UPLOAD_PATH + File.separator + folderPath + File.separator + uuid + "_" + fileName;
            log.info("saveFileName: {}", saveFileName);

            Path saveFilePath = Paths.get(saveFileName);

            try {
                //원본 파일 저장
                multipartFile.transferTo(saveFilePath);
                //썸네일 파일 이름은 중간에 _s를 넣어 구분
                String thumbnailFileName = UPLOAD_PATH + File.separator + folderPath + File.separator + "s_" +uuid + "_" + fileName;
                log.info("thumbnailFileName: {}", thumbnailFileName);
                //썸네일 저장
                File thumbnailFile = new File(thumbnailFileName);
                Thumbnailator.createThumbnail(saveFilePath.toFile(), thumbnailFile, 100, 100);

                resultDTOList.add(new UploadResultDTO(originalFileName, uuid, folderPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
    }

    private String makeFolderPath() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);

        File uploadPathFolder = new File(UPLOAD_PATH, folderPath);
        if (!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs();
        }
        return folderPath;
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName) {
        ResponseEntity<byte[]> result = null;
        try {
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("srcFileName: {}", srcFileName);

            File file = new File(UPLOAD_PATH, srcFileName);
            log.info("file: {}", file);

            HttpHeaders headers = new HttpHeaders();

            headers.add("Content-Type", Files.probeContentType(file.toPath()));
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @PostMapping("/deleteFile")
    public ResponseEntity<Boolean> removeFile(String fileName) {
        String srcFileName = "";
        boolean result = false;
        try {
            srcFileName = URLDecoder.decode(fileName, "UTF-8");
            File file = new File(UPLOAD_PATH, srcFileName);
            if (file.exists()) {
                result = file.delete();
            }

            File thumbnailFile = new File(UPLOAD_PATH, "s_" + srcFileName);
            if (thumbnailFile.exists()) {
                result = thumbnailFile.delete();
            }
            return new ResponseEntity<>(result, HttpStatus.OK);

        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
