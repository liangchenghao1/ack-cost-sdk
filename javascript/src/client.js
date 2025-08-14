/**
 * 客户端模块
 * @module client
 */

const axios = require('axios');
const CostV2Service = require('./costv2').CostV2Service;
const AllocationService = require('./allocation').AllocationService;

/**
 * 客户端配置类
 */
class Config {
    /**
     * 构造函数
     * @param {Object} options - 配置选项
     * @param {string} [options.apiServer=""] - API服务器地址
     * @param {number} [options.timeout=30] - 请求超时时间(秒)
     * @param {number} [options.retryCount=3] - 重试次数
     * @param {number} [options.retryWait=1] - 重试间隔(秒)
     */
    constructor(options = {}) {
        this.apiServer = options.apiServer || "";
        this.timeout = options.timeout || 30;
        this.retryCount = options.retryCount || 3;
        this.retryWait = options.retryWait || 1;
    }
}

/**
 * SDK客户端类
 */
class Client {
    /**
     * 构造函数
     * @param {Config} config - 客户端配置
     */
    constructor(config = new Config()) {
        this.config = config;
        
        // 创建HTTP客户端
        this.axiosInstance = axios.create({
            timeout: config.timeout * 1000
        });
        
        // 初始化各服务
        this.costV2 = new CostV2Service(this);
        this.allocation = new AllocationService(this);
    }
    
    /**
     * 发送HTTP请求，带重试机制
     * @param {Object} options - 请求选项
     * @returns {Promise<Object>} HTTP响应
     */
    async doRequest(options) {
        let lastError;
        
        // 重试机制
        for (let i = 0; i <= this.config.retryCount; i++) {
            try {
                const response = await this.axiosInstance.request(options);
                return response;
            } catch (error) {
                lastError = error;
                
                // 如果不是最后一次重试，等待一段时间后重试
                if (i < this.config.retryCount) {
                    await new Promise(resolve => setTimeout(resolve, this.config.retryWait * 1000));
                }
            }
        }
        
        // 如果所有重试都失败，抛出异常
        throw lastError;
    }
}

module.exports = {
    Client,
    Config
};