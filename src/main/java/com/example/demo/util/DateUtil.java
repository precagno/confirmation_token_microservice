package com.example.demo.util;

import java.time.LocalDateTime;

public class DateUtil {

  private DateUtil() {
  }

  public static LocalDateTime getTimeNow() {
    return LocalDateTime.now();
  }
}
