package com.xinsane.util;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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

public final class HttpApi {

    public static final class Result {

        private JsonObject data;
        Result(JsonObject data) {
            this.data = data;
        }

//        private int is_success = 0;
        public boolean isSuccess(String error_tag, String success_value) {
//            if (is_success != 0)
//                return is_success == 1;
//            boolean is = data.get(error_tag).getAsString().equals(success_value);
//            is_success = is ? 1 : -1;
//            return is;
            return data.get(error_tag).getAsString().equals(success_value);
        }
        public boolean isSuccess() {
            return isSuccess("error", "0");
        }

        public JsonObject getData() {
            return data;
        }
        public String getMessage(String message_tag) {
            return data.get(message_tag).getAsString();
        }
        public String getMessage() {
            return getMessage("msg");
        }

    }

    public static abstract class Listener {
        public void onResult(Result result) { }
        public void onFail(String msg) { }
    }

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();
                @Override
                public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }
                @Override
                public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            }).build();

    private Response response = null;
    private FormBody.Builder builder;
    private String url;
    private List<Listener> listeners = new LinkedList<>();

    public HttpApi(String url) {
        builder = new FormBody.Builder();
        this.url = url;
    }

    public Response getResponse() {
        return response;
    }

    public HttpApi add(String key, String value) {
        builder.add(key, value);
        return this;
    }

    public HttpApi addListener(Listener listener) {
        if (listener != null && !listeners.contains(listener))
            listeners.add(listener);
        return this;
    }

    public HttpApi removeListener(Listener listener) {
        listeners.remove(listener);
        return this;
    }

    public HttpApi post() {
        new Thread() {
            @Override
            public void run() {
                try {
                    RequestBody requestBody = builder.build();
                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    response = client.newCall(request).execute();
                    ResponseBody body = response.body();
                    String json = null;
                    if (body != null) {
                        json = body.string();
                        LogUtil.d("receive " + json, "HttpApi/post");
                    }
                    JsonObject res = (JsonObject) new JsonParser().parse(json);
                    Result result = new Result(res);
                    for (Listener listener : listeners)
                        listener.onResult(result);
                } catch (IOException e) {
                    e.printStackTrace();
                    for (Listener listener : listeners)
                        listener.onFail("网络错误");
                } catch (Exception all) {
                    all.printStackTrace();
                }
            }
        }.start();
        return this;
    }

}
