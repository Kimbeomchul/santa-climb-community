package com.mozzi.santa.config;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mozzi.santa.config.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadConvert(MultipartFile multipartFile, String dirName , String fileName) throws IOException {
        String returnUrl = "" ;
        // 파일검증
        try(InputStream inputStream = multipartFile.getInputStream()) {
            log.info("Content Type :  {}" , multipartFile.getContentType());
            if(!multipartFile.isEmpty()) {
                boolean isValid = FileUtils.validImgFile(inputStream);
                if(!isValid) {
                    // exception 처리
                    log.info("이미지 파일만 업로드가능 : 시도한 확장자 = {} ", multipartFile.getContentType());
                    throw new IOException();
                }else{
                    // 업로드 로직
                    File uploadFile = new File(fileName + ".jpeg");
                    uploadFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(uploadFile);
                    fos.write(multipartFile.getBytes());
                    fos.close();

                    returnUrl =  upload(uploadFile, dirName);
                    return returnUrl;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnUrl;
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }
}

