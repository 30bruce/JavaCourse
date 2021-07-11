package com.sk.week03.outbound.okhttp;

import com.sk.week03.filter.HeaderHttpResponseFilter;
import com.sk.week03.filter.HttpRequestFilter;
import com.sk.week03.filter.HttpResponseFilter;
import com.sk.week03.router.HttpEndpointRouter;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.sk.week03.router.RandomHttpEndpointRouter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import okhttp3.*;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkhttpOutboundHandler {
    private OkHttpClient okHttpClient;
    private HttpEndpointRouter router = new RandomHttpEndpointRouter();
    private List<String> backendUrls;
    private ExecutorService proxyService;
    private HttpResponseFilter filter = new HeaderHttpResponseFilter();

    public OkhttpOutboundHandler(List<String> backends) {

        this.backendUrls = backends.stream().map(this::formatUrl).collect(Collectors.toList());

        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);

        okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
    }

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, HttpRequestFilter filter) {
        String backendUrl = router.route(this.backendUrls);
        final String url = backendUrl + fullRequest.uri();
        filter.filter(fullRequest);
        proxyService.submit(() -> fetchGet(fullRequest, ctx, url));
    }

    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
        final Request request = new Request.Builder().url(url).build();
        final Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure...");
            }

            @Override
            public void onResponse(Call call, Response sourceResponse) {
                try {
                    FullHttpResponse response = null;
                    if (inbound != null) {

                        byte[] body = null;
                        if (sourceResponse.body() != null){
                            body = sourceResponse.body().bytes();
                        }
                        if (body == null){
                            return;
                        }

                        response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
                        response.headers().set("Content-Type", "application/json");
                        response.headers().setInt("Content-Length", (int)(sourceResponse.body().contentLength()));

                        filter.filter(response);

                        if (!HttpUtil.isKeepAlive(inbound)) {
                            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                        } else {
                            ctx.write(response);
                        }
                    }
                    ctx.flush();
                    ctx.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private String formatUrl(String backend) {
        return backend.endsWith("/") ? backend.substring(0, backend.length() - 1) : backend;
    }
}
