package com.easy.marketgo.common.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class WeComMediaUtils {

  protected static final List<String> supportedImagesType = Arrays.asList("jpg", "jpeg", "png");
  protected static final List<String> supportedVideoType = Arrays.asList("mp4");
  protected static final List<String> supportedVoiceType = Arrays.asList("amr");
  protected static final List<String> supportedFileType = Arrays.asList("doc", "docx", "ppt", "pptx", "pdf", "xls", "xlsx");

  public static boolean supportedTempMediaType(String fileType, MediaTypeEnum typeEnum) {
    fileType = fileType.toLowerCase();
    if (Objects.equals(typeEnum.name(), MediaTypeEnum.IMAGE.name())) {
      return supportedImagesType.contains(fileType);
    }
    if (Objects.equals(typeEnum.name(), MediaTypeEnum.VIDEO.name())) {
      return supportedVideoType.contains(fileType);
    }
    if (Objects.equals(typeEnum.name(), MediaTypeEnum.VOICE.name())) {
      return supportedVoiceType.contains(fileType);
    }
    if (Objects.equals(typeEnum.name(), MediaTypeEnum.FILE.name())) {
      return supportedFileType.contains(fileType);
    }
    return true;
  }

  public static boolean supportedTempMediaSizeLimit(long length, MediaTypeEnum typeEnum) {
    // 5B
    if (length < 5) {
      return false;
    }
    // 10M
    if (Objects.equals(typeEnum.name(), MediaTypeEnum.IMAGE.name())) {
      return length <= 10 * 1024 * 1024;
    }
    // 10M
    if (Objects.equals(typeEnum.name(), MediaTypeEnum.VIDEO.name())) {
      return length <= 10 * 1024 * 1024;
    }
    // 2M
    if (Objects.equals(typeEnum.name(), MediaTypeEnum.VOICE.name())) {
      return length <= 2 * 1024 * 1024;
    }
    // 20M
    return length <= 20 * 1024 * 1024;
  }

  public static boolean supportedPermanentMediaType(String fileType) {
    return supportedImagesType.contains(fileType);
  }

  public static boolean supportedPermanentMediaSizeLimit(long length) {
    return length >= 5 && length <= 2 * 1024 * 1024;
  }

  public enum MediaTypeEnum {
    IMAGE,
    VOICE,
    VIDEO,
    FILE
  }

}
