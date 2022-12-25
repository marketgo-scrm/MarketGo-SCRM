package com.easy.marketgo.core.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.CollectionUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : kevinwang
 * @version : 1.0
 * @data : 2022-05-26 17:35
 * Describe:
 */
@Slf4j
public class OkHttpUtils {
    public final static int HTTP_READ_TIMEOUT = 120;
    public final static int HTTP_CONNECT_TIMEOUT = 60;
    public final static int HTTP_WRITE_TIMEOUT = 60;
    public static final MediaType HTTP_MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType HTTP_MEDIA_TYPE_XML = MediaType.parse("application/xml; charset=utf-8");
    private static OkHttpUtils mInstance = new OkHttpUtils();
    private OkHttpClient mOkHttpClient;

    /**
     * 自定义网络回调接口
     */
    public interface NetCall {
        void success(Call call, Response response) throws IOException;

        void failed(Call call, IOException e);
    }

    private OkHttpUtils() {
    }

    private OkHttpClient getOkHttpClient() throws Exception {
        if (mOkHttpClient != null) {
            return mOkHttpClient;
        }

        TrustManager[] trustManagers = new TrustManager[]{new TrustAllCerts()};

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //读取超时
        clientBuilder.readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS);
        //连接超时
        clientBuilder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        //写入超时
        clientBuilder.writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS);
        //支持HTTPS请求，跳过证书验证
        clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]);
        clientBuilder.hostnameVerifier((hostname, session) -> true);
        return mOkHttpClient = clientBuilder.build();
    }

    /**
     * 单例模式获取OkHttpUtil
     *
     * @return
     */
    public static OkHttpUtils getInstance() {
        return mInstance;
    }

    /**
     * get请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public String getDataSync(String url, Map<String, String> paramsHeader) throws Exception {
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (!CollectionUtils.isEmpty(paramsHeader)) {
            for (Map.Entry<String, String> entry : paramsHeader.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = builder.get().url(urlBuilder.build()).build();
        //2 将Request封装为Call
        Call call = getOkHttpClient().newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * get请求，同步方式，获取网络数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @return
     */
    public String getData(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (!CollectionUtils.isEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        if (!CollectionUtils.isEmpty(header)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        Request request = builder.get().url(urlBuilder.build()).build();
        //2 将Request封装为Call
        Call call = getOkHttpClient().newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * post请求，同步方式，提交数据，是在主线程中执行的，需要新起线程，将其放到子线程中执行
     *
     * @param url
     * @param bodyParams
     * @return
     */
    public Response postDataSync(String url, Map<String, String> paramsHeader, Map<String, String> bodyParams) throws Exception {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (!CollectionUtils.isEmpty(paramsHeader)) {
            for (Map.Entry<String, String> entry : paramsHeader.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder.post(body).url(urlBuilder.build()).build();
        //3 将Request封装为Call
        Call call = getOkHttpClient().newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * get请求，异步方式，获取网络数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url
     * @param netCall
     * @return
     */
    public void getDataAsyn(String url, final NetCall netCall) throws Exception {
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder.get().url(url).build();
        //2 将Request封装为Call
        Call call = getOkHttpClient().newCall(request);
        //3 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);

            }
        });
    }

    /**
     * post请求，异步方式，提交数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url
     * @param bodyParams
     * @param netCall
     */
    public void postDataAsyn(String url, Map<String, String> bodyParams, final NetCall netCall) throws Exception {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        //3 将Request封装为Call
        Call call = getOkHttpClient().newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);

            }
        });
    }

    /**
     * post的请求参数，构造RequestBody
     *
     * @param bodyParams
     * @return
     */
    private RequestBody setRequestBody(Map<String, String> bodyParams) {
        RequestBody body = null;
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        if (bodyParams != null) {
            Iterator<String> iterator = bodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                formEncodingBuilder.add(key, bodyParams.get(key));
                // log.info("post_Params=== {} ==== {}" + key, bodyParams.get(key));
            }
        }
        body = formEncodingBuilder.build();
        return body;

    }

    public String postXml(String url, Map<String, String> paramsHeader, String xml) throws Exception {
        RequestBody body = RequestBody.create(HTTP_MEDIA_TYPE_XML, xml);

        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (!CollectionUtils.isEmpty(paramsHeader)) {
            for (Map.Entry<String, String> entry : paramsHeader.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder
                .url(urlBuilder.build())
                .post(body)
                .build();
        Response response = getOkHttpClient().newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public String postJsonSync(String url, Map<String, String> paramsHeader, String json) throws Exception {
        RequestBody body = RequestBody.create(HTTP_MEDIA_TYPE_JSON, json);

        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (!CollectionUtils.isEmpty(paramsHeader)) {
            for (Map.Entry<String, String> entry : paramsHeader.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder
                .url(urlBuilder.build())
                .post(body)
                .build();
        Response response = getOkHttpClient().newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public void postJsonAsyn(String url, String json, final NetCall netCall) throws Exception {
        RequestBody body = RequestBody.create(HTTP_MEDIA_TYPE_JSON, json);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.post(body).url(url).build();
        //3 将Request封装为Call
        Call call = getOkHttpClient().newCall(request);
        //4 执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                netCall.failed(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                netCall.success(call, response);

            }
        });
    }

    /**
     * 第二种方式
     *
     * @param url
     * @param fileData
     * @throws IOException
     */
    public String PostFileDataSync(String url, Map<String, String> paramsHeader, String fileName, byte[] fileData) throws Exception {
        MultipartBody.Builder builder1 = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder1.addFormDataPart("files", fileName, RequestBody.create(MediaType.parse("application/octet-stream"),
                fileData));
        RequestBody body = builder1.build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        Request.Builder requestBuilder = new Request.Builder();

        if (!CollectionUtils.isEmpty(paramsHeader)) {
            for (Map.Entry<String, String> entry : paramsHeader.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = requestBuilder
                .url(urlBuilder.build())
                .method("POST", body)
                .addHeader("accept", "*/*")
                .addHeader("Content-Type", "multipart/form-data")
                .build();


        Response response = getOkHttpClient().newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 用于信任所有证书
     */
    class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static void main(String[] args) throws Exception {
        String response = OkHttpUtils.getInstance().getDataSync("http://www.baidu.com", null);
        System.out.println(response);
    }
}

