package com.mozzi.santa.controller;

import com.mozzi.santa.config.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.util.UUID;

import static com.mozzi.santa.config.Constants.FILE_MAX_SIZE;
import static com.mozzi.santa.config.Constants.SESSION_NAME;

@Slf4j
@RequiredArgsConstructor
@Controller
public class S3Controller {
    private final S3Uploader s3Uploader;


    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
        // 파일크기 9M 이상일경우
        if(multipartFile.getSize() > FILE_MAX_SIZE) {
            return "redirect:/board/list";
        }
        String fileUuid = UUID.randomUUID().toString();
        return s3Uploader.uploadConvert(multipartFile, "static" ,fileUuid);
    }

    @PostMapping("/upload/member")
    @ResponseBody
    public String uploadMember(@RequestParam("data") MultipartFile multipartFile, @SessionAttribute(name= SESSION_NAME, required = false) String socialId) throws IOException {
        // 파일크기 9M 이상일경우
        if(multipartFile.getSize() > FILE_MAX_SIZE) {
            return "redirect:/profile/edit/" + socialId;
        }
        String fileUuid = UUID.randomUUID().toString();
        return s3Uploader.uploadConvert(multipartFile, "member" ,fileUuid);
    }
}
