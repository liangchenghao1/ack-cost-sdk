/**
 * 错误处理模块
 * @module errors
 */

// 错误码定义
const ERROR_CODES = {
    BadRequest: "BadRequest",
    Unauthorized: "Unauthorized",
    Forbidden: "Forbidden",
    NotFound: "NotFound",
    TooManyRequests: "TooManyRequests",
    InternalError: "InternalError",
    ServiceUnavailable: "ServiceUnavailable"
};

/**
 * 成本SDK基础错误类
 */
class CostError extends Error {
    /**
     * 构造函数
     * @param {string} code - 错误码
     * @param {string} message - 错误消息
     * @param {any} details - 错误详情
     */
    constructor(code, message, details = null) {
        super(message);
        this.code = code;
        this.message = message;
        this.details = details;
        this.name = 'CostError';
    }

    /**
     * 获取错误的字符串表示
     * @returns {string}
     */
    toString() {
        return `${this.code}: ${this.message}`;
    }
}

/**
 * 根据HTTP状态码创建错误
 * @param {number} statusCode - HTTP状态码
 * @param {any} details - 错误详情
 * @returns {CostError}
 */
function httpErrorFromStatusCode(statusCode, details = null) {
    const errorMap = {
        400: [ERROR_CODES.BadRequest, "Bad Request"],
        401: [ERROR_CODES.Unauthorized, "Unauthorized"],
        403: [ERROR_CODES.Forbidden, "Forbidden"],
        404: [ERROR_CODES.NotFound, "Not Found"],
        429: [ERROR_CODES.TooManyRequests, "Too Many Requests"],
        500: [ERROR_CODES.InternalError, "Internal Server Error"],
        503: [ERROR_CODES.ServiceUnavailable, "Service Unavailable"]
    };

    const [code, message] = errorMap[statusCode] || [ERROR_CODES.InternalError, `Unknown error with status code ${statusCode}`];
    return new CostError(code, message, details);
}

module.exports = {
    CostError,
    ERROR_CODES,
    httpErrorFromStatusCode
};