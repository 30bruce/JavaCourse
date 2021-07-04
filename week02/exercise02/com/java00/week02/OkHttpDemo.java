package com.java00.week02;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OkHttpDemo {
    static OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws IOException {
        String url = "http://localhost:8801";
        String content = doRequest(url);
        System.out.println("response:\n" + content);

        client = null;
    }

    // 发起请求
    public static String doRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}

