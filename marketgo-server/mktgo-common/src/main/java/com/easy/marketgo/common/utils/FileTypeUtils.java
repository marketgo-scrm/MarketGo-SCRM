package com.easy.marketgo.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@UtilityClass
public class FileTypeUtils {

  public String getFileType(byte[] bytes, String filename) {
    String bytesToHexString = bytesToHexString(bytes);
    if (StringUtils.isBlank(bytesToHexString)) {
      return null;
    }
    if (bytesToHexString.startsWith(FileTye.DOC_XLS_PPT.getValue())) {
      String extension = FilenameUtils.getExtension(filename);
      if (Objects.equals(extension, "doc")) {
        return "WORD";
      } else if (Objects.equals(extension, "xls")) {
        return "EXCEL";
      } else if (Objects.equals(extension, "ppt")) {
        return "PPT";
      }
    } else if (bytesToHexString.startsWith(FileTye.DOCX_XLSX_PPTX.getValue())) {
      String extension = FilenameUtils.getExtension(filename);
      if (Objects.equals(extension, "docx")) {
        return "WORD";
      } else if (Objects.equals(extension, "xlsx")) {
        return "EXCEL";
      } else if (Objects.equals(extension, "pptx")) {
        return "PPT";
      }
    }
    for (FileTye fileTye : FileTye.values()) {
      if (bytesToHexString.startsWith(fileTye.getValue())) {
        return fileTye.name();
      }
    }
    return FilenameUtils.getExtension(filename).toUpperCase();
  }


  public static String bytesToHexString(byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder();
    if (null == bytes || bytes.length <= 0){
      return null;
    }
    for (int i = 0; i < bytes.length; i++){
      int v = bytes[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hv);
    }
    return stringBuilder.toString().toLowerCase();
  }

  private enum FileTye {

    JPEG("ffd8ff"),
    PNG("89504e470d0a1a0a"),
    MP4("0000001c66747970"),
    PDF("255044462d312e"),
    /**
     * doc、xls、ppt
     */
    DOC_XLS_PPT("d0cf11e0a1b11ae1"),
    /**
     * docx、xlsx、pptx
     */
    DOCX_XLSX_PPTX("504b0304")
    ;

    private String value;

    FileTye(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static FileTye fromValue(String value) {
      for (FileTye fileTye : FileTye.values()) {
        if (Objects.equals(fileTye.getValue(), value)) {
          return fileTye;
        }
      }
      return null;
    }

  }

}
