package com.xinsane.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xinsane on 2018/3/2.
 */

public class HttpApi {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }
                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            }).build();

    private Response response = null;
    private FormBody.Builder builder;
    private String url;

    public HttpApi(String url) {
        builder = new FormBody.Builder();
        this.url = url;
    }

    public HttpApi add(String key, String value) {
        builder.add(key, value);
        return this;
    }

    public Response post() {
        try {
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder().url(url).post(requestBody).build();
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String postForString() {
        post();
        if (response == null)
            return "";
        ResponseBody body = response.body();
        try {
            if (body != null) {
                String json = body.string();
                LogUtil.d(json, "HttpApi/postForString");
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
