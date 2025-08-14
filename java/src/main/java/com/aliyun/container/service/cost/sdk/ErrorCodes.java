package com.aliyun.container.service.cost.sdk;

/**
 * 错误码定义
 */
public class ErrorCodes {
    /** 请求参数错误 */
    public static final String BadRequest = "BadRequest";
    
    /** 未授权访问 */
    public static final String Unauthorized = "Unauthorized";
    
    /** 禁止访问 */
    public static final String Forbidden = "Forbidden";
    
    /** 资源未找到 */
    public static final String NotFound = "NotFound";
    
    /** 请求过于频繁 */
    public static final String TooManyRequests = "TooManyRequests";
    
    /** 内部服务器错误 */
    public static final String InternalError = "InternalError";
    
    /** 服务不可用 */
    public static final String ServiceUnavailable = "ServiceUnavailable";
}