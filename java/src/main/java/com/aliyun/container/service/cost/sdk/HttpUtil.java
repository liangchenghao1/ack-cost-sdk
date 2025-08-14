package com.aliyun.container.service.cost.sdk;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP工具类
 */
public class HttpUtil {
    private final OkHttpClient client;

    /**
     * 构造函数
     * @param config 客户端配置
     */
    public HttpUtil(Config config) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(config.getTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getTimeout(), TimeUnit.SECONDS)
                .writeTimeout(config.getTimeout(), TimeUnit.SECONDS)
                .build();
    }

    /**
     * 发送HTTP请求，带重试机制
     * @param request HTTP请求
     * @param config 客户端配置
     * @return HTTP响应
     * @throws CostError 请求失败
     */
    public Response doRequest(Request request, Config config) throws CostError {
        Exception lastException = null;

        // 重试机制
        for (int i = 0; i <= config.getRetryCount(); i++) {
            try {
                Response response = client.newCall(request).execute();
                return response;
            } catch (Exception e) {
                lastException = e;
                
                // 如果不是最后一次重试，等待一段时间后重试
                if (i < config.getRetryCount()) {
                    try {
                        Thread.sleep(config.getRetryWait() * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new CostError(ErrorCodes.InternalError, "Interrupted during retry wait", ie);
                    }
                }
            }
        }

        // 如果所有重试都失败，抛出异常
        throw new CostError(ErrorCodes.InternalError, "Failed to send request after retries", lastException);
    }
}