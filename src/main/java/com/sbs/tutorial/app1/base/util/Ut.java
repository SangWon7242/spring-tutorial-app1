package com.sbs.tutorial.app1.base.util;

import org.apache.tika.Tika;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Ut {

  public static class date {
    public static String getCurrentDateFormatted(String pattern) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
      String formatted = LocalDateTime.now().format(formatter);

      return formatted;
    }
  }

  public static class file {
    public static String getExt(String fileName) {
      return Optional.ofNullable(fileName)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(f.lastIndexOf(".") + 1))
          .orElse("");
    }

    public static String downloadImg(String url, String filePath) {
      new File(filePath).getParentFile().mkdirs();
      // c:/spring-temp/app1/member/2025_10_12/abcd.jpg

      byte[] imageBytes = new RestTemplate().getForObject(url, byte[].class);
      try {
        Files.write(Paths.get(filePath), imageBytes);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      String mimeType = null;
      try {
        mimeType = new Tika().detect(new File(filePath));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      String ext = mimeType.replaceAll("image/", "");
      ext = ext.replaceAll("jpeg", "jpg");

      String newFilePath = filePath + "." + ext;

      new File(filePath).renameTo(new File(newFilePath));

      return newFilePath;
    }

    public static String getFileExtTypeCodeFromFileExt(String ext) {
      return switch (ext) {
        case "jpeg", "jpg", "gif", "png" -> "img"; // 이미지
        case "mp4", "avi", "mov" -> "video"; // 비디오
        case "mp3" -> "audio"; // 오디오
        default -> "etc";
      };
    }

    public static String getFileExtType2CodeFromFileExt(String ext) {
      return switch (ext) {
        case "jpeg", "jpg" -> "jpg";
        case "gif", "png", "mp4", "mov", "mp3", "avi" -> ext;
        default -> "etc";
      };
    }
  }
}
