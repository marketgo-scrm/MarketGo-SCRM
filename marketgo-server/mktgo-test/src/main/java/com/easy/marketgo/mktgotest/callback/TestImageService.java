package com.easy.marketgo.mktgotest.callback;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 9/1/22 9:25 AM
 * Describe:
 */
public class TestImageService {

    private static String mediaToFiles(byte[] fileData, Long fileSize, String originalFilename, String formatType) {
        String base64 = "";
        String fileName = System.currentTimeMillis() + originalFilename;
        //将MultipartFile转为File
        File files = new File(fileName);
        OutputStream out = null;
        try {
            //获取文件流，以文件流的方式输出到新文件
            out = new FileOutputStream(files);
            for (int i = 0; i < fileData.length; i++) {
                out.write(fileData[i]);
            }
        } catch (IOException e) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        try {
            BufferedImage output = Thumbnails.of(files).size(200, 200).asBufferedImage();
            base64 = imageToBase64(output, formatType);
        } catch (Exception e) {
            return base64;
        }
        return StringUtils.isBlank(base64) ? java.util.Base64.getEncoder().encodeToString(fileData) : base64;
    }

    // BufferedImage转换成base64，在这里需要设置图片格式，如下是jpg格式图片：
    public static String imageToBase64(BufferedImage bufferedImage, String formatType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, formatType, baos);
        } catch (IOException e) {
        }
        return Base64.getEncoder().encodeToString((baos.toByteArray()));
    }


    public static void main(String[] args) throws IOException {

        byte[] bytes = FileUtils.readFileToByteArray(new File("/Users/kevinwang/analys_sys/ea设计图/640-420.png"));
        String base64 = mediaToFiles(bytes, Long.valueOf(bytes.length), "640-420.png", "png");
    }


}
