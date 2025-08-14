package com.aliyun.container.service.cost.sdk;

/**
 * 客户端配置类
 */
public class Config {
    private String apiServer = "";
    private int timeout = 30;
    private int retryCount = 3;
    private int retryWait = 1;

    /**
     * 获取API服务器地址
     * @return API服务器地址
     */
    public String getApiServer() {
        return apiServer;
    }

    /**
     * 设置API服务器地址
     * @param apiServer API服务器地址
     * @return Config对象
     */
    public Config setApiServer(String apiServer) {
        this.apiServer = apiServer;
        return this;
    }

    /**
     * 获取请求超时时间(秒)
     * @return 超时时间
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置请求超时时间(秒)
     * @param timeout 超时时间
     * @return Config对象
     */
    public Config setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 获取重试次数
     * @return 重试次数
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * 设置重试次数
     * @param retryCount 重试次数
     * @return Config对象
     */
    public Config setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * 获取重试间隔(秒)
     * @return 重试间隔
     */
    public int getRetryWait() {
        return retryWait;
    }

    /**
     * 设置重试间隔(秒)
     * @param retryWait 重试间隔
     * @return Config对象
     */
    public Config setRetryWait(int retryWait) {
        this.retryWait = retryWait;
        return this;
    }
}