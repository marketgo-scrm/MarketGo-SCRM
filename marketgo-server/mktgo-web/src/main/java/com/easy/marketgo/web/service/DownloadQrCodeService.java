package com.easy.marketgo.web.service;

import com.easy.marketgo.common.exception.CommonException;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-07-31 22:24:55
 * @description : 二维码下载
 */
@Component
@Log4j2
public class DownloadQrCodeService {

    private static final String FILE_PREFIX = "二维码下载";

    public void download(HttpServletResponse response, String uri, String fileName) {
        ServletOutputStream out = null;
        InputStream inputStream = null;
        try {
            //获取外部文件流
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6 * 1000);
            inputStream = conn.getInputStream();
            /**
             * 输出文件到浏览器
             */
            int len = 0;
            // 输出 下载的响应头，如果下载的文件是中文名，文件名需要经过url编码
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Cache-Control", "no-cache");
            out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.info("prCox={},下载异常",FILE_PREFIX);
            throw new CommonException("推广二维码下载异常");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.info("prefix={},资源流关闭异常",FILE_PREFIX);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    log.info("prefix={},资源流关闭异常",FILE_PREFIX);
                }
            }
        }
    }

}
