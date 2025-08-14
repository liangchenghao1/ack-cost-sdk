package com.aliyun.container.service.cost.sdk;

/**
 * 成本SDK基础错误类
 */
public class CostError extends Exception {
    private final String code;
    private final String message;
    private final Throwable details;

    /**
     * 构造函数
     * @param code 错误码
     * @param message 错误消息
     * @param details 错误详情
     */
    public CostError(String code, String message, Throwable details) {
        super(message, details);
        this.code = code;
        this.message = message;
        this.details = details;
    }

    /**
     * 获取错误码
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取错误消息
     * @return 错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 获取错误详情
     * @return 错误详情
     */
    public Throwable getDetails() {
        return details;
    }

    /**
     * 获取错误的字符串表示
     * @return 错误字符串
     */
    @Override
    public String toString() {
        return code + ": " + message;
    }
}