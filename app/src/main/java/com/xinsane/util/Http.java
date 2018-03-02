package com.xinsane.util;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xinsane on 2018/2/15.
 */

public final class Http {

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Response response = null;

    public String get(String url) {
        String result = "";
        try {
            Request request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null)
                result = body.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String postJson(String url, String json) {
        String result = "";
        try {
            RequestBody requestBody = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(requestBody).build();
            response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null)
                result = body.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String post(String url, Map<String, String> map) {
        String result = "";
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : map.keySet())
                builder.add(key, map.get(key));
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder().url(url).post(requestBody).build();
            response = client.newCall(request).execute();
            ResponseBody body = response.body();
            if (body != null)
                result = body.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Response getResponse() {
        return response;
    }
}
