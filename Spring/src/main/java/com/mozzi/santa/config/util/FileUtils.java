package com.mozzi.santa.config.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
@Slf4j
public class FileUtils {
    private static final Tika tika = new Tika();

    public static boolean validImgFile(InputStream inputStream) {
        try {
            List<String> notValidTypeList = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/x-windows-bmp");
            String mimeType = tika.detect(inputStream);

            boolean isValid = notValidTypeList.stream().anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));

            return isValid;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

