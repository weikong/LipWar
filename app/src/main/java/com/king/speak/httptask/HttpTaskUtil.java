package com.king.speak.httptask;

import com.king.speak.config.UrlConstants;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by kongwei on 2017/3/10.
 */

public class HttpTaskUtil {

    private ResultListener resultListener;

    private HttpTaskUtil mInstance;

    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;
    public static final OkHttpClient client = new OkHttpClient();
//            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
//            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
//            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
//            .build();

    public static final MediaType JSONMediaType = MediaType.parse("application/json; charset=utf-8");

    public static String post(String url, String json) throws IOException {
        client.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        client.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);//设置写的超时时间
        client.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        RequestBody body = RequestBody.create(JSONMediaType, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

//    public HttpTaskUtil getInstance() {
//        if (mInstance == null) {
//            synchronized (HttpTaskUtil.class) {
//                if (mInstance == null) {
//                    mInstance = new HttpTaskUtil();
//                }
//            }
//        }
//        return mInstance;
//    }

    public interface ResultListener {
        public void onResponse(String response);

        public void onFailure(Request request, Exception e);
    }

    public HttpTaskUtil setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
        return this;
    }

    public void googleTranslate(String content, String sourceLanguage, String targetLanguage) {
        try {
            OkHttpClientManager.Param contentParam = new OkHttpClientManager.Param("content", content);
            OkHttpClientManager.Param sourceLanguageParam = new OkHttpClientManager.Param("sourceLanguage", sourceLanguage);
            OkHttpClientManager.Param targetLanguageParam = new OkHttpClientManager.Param("targetLanguage", targetLanguage);
            OkHttpClientManager.getInstance()._postAsyn(UrlConstants.HTTP_GOOGLE_TRANSLATE, new OkHttpClientManager.StringCallback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    if (resultListener != null)
                        resultListener.onFailure(request, e);
                }

                @Override
                public void onResponse(String response) {
                    if (resultListener != null)
                        resultListener.onResponse(response);
                }
            }, contentParam, sourceLanguageParam, targetLanguageParam);
        } catch (Exception e) {
            e.printStackTrace();
            if (resultListener != null)
                resultListener.onFailure(null, e);
        }
    }

    public OkHttpClientManager.Param setLocateParam(String key, String value) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param(key, value);
        return param;
    }
}
