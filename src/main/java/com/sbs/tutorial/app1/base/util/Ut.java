package com.sbs.tutorial.app1.base.util;

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
  }
}
