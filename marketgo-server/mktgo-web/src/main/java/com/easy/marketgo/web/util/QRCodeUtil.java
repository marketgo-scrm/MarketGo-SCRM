package com.easy.marketgo.web.util;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 8/19/22 7:17 PM
 * Describe:
 */
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

public class QRCodeUtil {
    private static final String CHARSET = "utf-8";
    private static final String IMG_FORMAT_NAME = "PNG";

    private static final Integer QR_CODE_SIZE = 300;
    private static final Integer WIDTH = 60;
    private static final Integer HEIGHT = 60;

    private static BufferedImage createImage(String content, String imgPath, Boolean needCompress) throws WriterException, IOException {

        Hashtable<EncodeHintType, Object> ht = new Hashtable<EncodeHintType, Object>();
        ht.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        ht.put(EncodeHintType.CHARACTER_SET, CHARSET);
        ht.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, ht);
        int height = bitMatrix.getHeight();
        int width = bitMatrix.getWidth();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, bitMatrix.get(i,j) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }

        QRCodeUtil.insertImg(image, imgPath, needCompress);

        return image;
    }

    private static void insertImg(BufferedImage source, String imgPath, Boolean needCompress) throws IOException {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println("" + imgPath + "   该文件不存在！");
            throw new FileNotFoundException();
        }

        Image src = ImageIO.read(file);
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) {
            if (width > WIDTH) width = WIDTH;
            if (height > WIDTH) height = WIDTH;
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = img.createGraphics();
            g.dispose();
            src = image;
        }

        Graphics2D graph = source.createGraphics();
        int x = (QR_CODE_SIZE - width) / 2;
        int y = (QR_CODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, height, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    public static void encode(String content, String imgPath, String destPath, Boolean needCompress) throws IOException, WriterException, ClassNotFoundException {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        mkdir(destPath);

        ImageIO.write(image, IMG_FORMAT_NAME, new File(destPath));
    }

    public static BufferedImage encode(String content, String imgPath, Boolean needCompress) throws IOException, WriterException, ClassNotFoundException {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        return image;
    }

    public static void encode(String content, String imgPath, OutputStream outputStream, Boolean needCompress) throws IOException, WriterException, ClassNotFoundException {
        BufferedImage image = QRCodeUtil.createImage(content, imgPath, needCompress);
        ImageIO.write(image, IMG_FORMAT_NAME, outputStream);
    }

    public static void encode(String content, String destPath) throws ClassNotFoundException, IOException, WriterException {
        QRCodeUtil.encode(content, null, destPath, false);
    }

    public static void encode(String content, OutputStream outputStream) throws ClassNotFoundException, IOException, WriterException {
        QRCodeUtil.encode(content, null, outputStream, false);
    }

    public static String decode(File file) throws IOException, NotFoundException {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            throw new IOException();
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> ht = new Hashtable<DecodeHintType, Object>();
        ht.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, ht);
        return result.getText();
    }

    public static String decode(String path) throws IOException, NotFoundException {
        return QRCodeUtil.decode(new File(path));
    }

    private static void mkdir(String destPath) {
        File file = new File(destPath);

        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }
}
