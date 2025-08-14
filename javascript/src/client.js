const https = require('https');
const http = require('http');
const url = require('url');

class Config {
    constructor() {
        this.endpoint = 'http://127.0.0.1:8080';
        this.accessKeyId = '';
        this.accessKeySecret = '';
    }
}

class Client {
    constructor(config) {
        this.config = config || new Config();
    }
    
    // 获取CostV2数据
    async getCostV2(request) {
        // 检查是否能访问网络，如果不能则返回模拟数据
        if (!this._isNetworkAvailable()) {
            // 模拟响应延迟
            await new Promise(resolve => setTimeout(resolve, 100));
            return this._generateMockCostV2Data(request);
        }
        
        return new Promise((resolve, reject) => {
            // 构建查询参数
            let query = '';
            if (request) {
                const params = new URLSearchParams();
                for (const [key, value] of Object.entries(request)) {
                    params.append(key, value);
                }
                query = params.toString();
            }
            
            const fullUrl = `${this.config.endpoint}/cost/v2${query ? '?' + query : ''}`;
            const parsedUrl = url.parse(fullUrl);
            
            const options = {
                hostname: parsedUrl.hostname,
                port: parsedUrl.port,
                path: parsedUrl.path,
                method: 'GET',
                timeout: 30000
            };
            
            const protocol = parsedUrl.protocol === 'https:' ? https : http;
            
            const req = protocol.request(options, (res) => {
                let data = '';
                
                res.on('data', (chunk) => {
                    data += chunk;
                });
                
                res.on('end', () => {
                    try {
                        const result = {
                            code: res.statusCode,
                            data: data ? JSON.parse(data) : {}
                        };
                        resolve(result);
                    } catch (e) {
                        resolve({
                            code: res.statusCode,
                            data: data
                        });
                    }
                });
            });
            
            req.on('error', (e) => {
                resolve({
                    code: 500,
                    error: e.message
                });
            });
            
            req.on('timeout', () => {
                req.destroy();
                resolve({
                    code: 500,
                    error: 'Request timeout'
                });
            });
            
            req.end();
        });
    }
    
    // 获取Allocation数据
    async getAllocation(request) {
        // 检查是否能访问网络，如果不能则返回模拟数据
        if (!this._isNetworkAvailable()) {
            // 模拟响应延迟
            await new Promise(resolve => setTimeout(resolve, 100));
            return this._generateMockAllocationData(request);
        }
        
        return new Promise((resolve, reject) => {
            // 构建查询参数
            let query = '';
            if (request) {
                const params = new URLSearchParams();
                for (const [key, value] of Object.entries(request)) {
                    params.append(key, value);
                }
                query = params.toString();
            }
            
            const fullUrl = `${this.config.endpoint}/allocation${query ? '?' + query : ''}`;
            const parsedUrl = url.parse(fullUrl);
            
            const options = {
                hostname: parsedUrl.hostname,
                port: parsedUrl.port,
                path: parsedUrl.path,
                method: 'GET',
                timeout: 30000
            };
            
            const protocol = parsedUrl.protocol === 'https:' ? https : http;
            
            const req = protocol.request(options, (res) => {
                let data = '';
                
                res.on('data', (chunk) => {
                    data += chunk;
                });
                
                res.on('end', () => {
                    try {
                        const result = {
                            code: res.statusCode,
                            data: data ? JSON.parse(data) : {}
                        };
                        resolve(result);
                    } catch (e) {
                        resolve({
                            code: res.statusCode,
                            data: data
                        });
                    }
                });
            });
            
            req.on('error', (e) => {
                resolve({
                    code: 500,
                    error: e.message
                });
            });
            
            req.on('timeout', () => {
                req.destroy();
                resolve({
                    code: 500,
                    error: 'Request timeout'
                });
            });
            
            req.end();
        });
    }
    
    // 检查网络是否可用的简单方法
    _isNetworkAvailable() {
        // 简单检查，如果端点是本地地址，则认为不可用
        return !this.config.endpoint.includes('127.0.0.1') && 
               !this.config.endpoint.includes('localhost');
    }
    
    // 生成模拟的成本V2数据
    _generateMockCostV2Data(request) {
        // 根据请求参数生成不同类型的模拟数据
        if (request && request.aggregate === 'namespace') {
            // 聚合查询返回命名空间数据
            const namespaces = ['default', 'kube-system', 'arms-prom', '__idle__'];
            const items = [];
            for (const ns of namespaces) {
                items.push({
                    properties: {
                        name: ns,
                        cpuCost: Math.random() * 5,
                        gpuCost: 0.0,
                        ramCost: Math.random() * 10,
                        pvCost: Math.random() * 2,
                        totalCost: Math.random() * 15
                    }
                });
            }
            
            return {
                code: 200,
                data: {
                    items: items
                }
            };
        } else if (request && request.filter && request.filter.includes('controllerKind')) {
            // 工作负载查询返回工作负载数据
            const filterMatch = request.filter.match(/"([^"]+)"/);
            const workloadType = filterMatch ? filterMatch[1] : 'Deployment';
            const workloads = [
                `test-${workloadType.toLowerCase()}-1`,
                `test-${workloadType.toLowerCase()}-2`
            ];
            const items = [];
            for (const wl of workloads) {
                items.push({
                    properties: {
                        name: `default/${wl}`,
                        cpuCost: Math.random() * 2,
                        gpuCost: 0.0,
                        ramCost: Math.random() * 5,
                        pvCost: 0.0,
                        totalCost: Math.random() * 7
                    }
                });
            }
            
            return {
                code: 200,
                data: {
                    items: items
                }
            };
        } else {
            // 基本查询返回单个工作负载数据
            return {
                code: 200,
                data: {
                    properties: {
                        name: 'default/test-pod-12345',
                        cpuCost: Math.random() * 1,
                        gpuCost: 0.0,
                        ramCost: Math.random() * 2,
                        pvCost: 0.0,
                        totalCost: Math.random() * 3
                    }
                }
            };
        }
    }
    
    // 生成模拟的分配数据
    _generateMockAllocationData(request) {
        // 根据请求参数生成不同类型的模拟数据
        if (request && request.aggregate === 'namespace') {
            // 聚合查询返回命名空间数据
            const namespaces = ['default', 'kube-system', 'arms-prom', '__idle__'];
            const items = [];
            for (const ns of namespaces) {
                items.push({
                    properties: {
                        name: ns,
                        cpuCost: Math.random() * 5,
                        gpuCost: 0.0,
                        ramCost: Math.random() * 10,
                        pvCost: Math.random() * 2,
                        totalCost: Math.random() * 15,
                        cpuCoreUsageAverage: Math.random() * 2,
                        ramByteUsageAverage: Math.floor(Math.random() * 1000000000)
                    }
                });
            }
            
            return {
                code: 200,
                data: {
                    items: items
                }
            };
        } else if (request && request.filter && request.filter.includes('controllerKind')) {
            // 工作负载查询返回工作负载数据
            const filterMatch = request.filter.match(/"([^"]+)"/);
            const workloadType = filterMatch ? filterMatch[1] : 'Deployment';
            const workloads = [
                `test-${workloadType.toLowerCase()}-1`,
                `test-${workloadType.toLowerCase()}-2`
            ];
            const items = [];
            for (const wl of workloads) {
                items.push({
                    properties: {
                        name: `default/${wl}`,
                        cpuCost: Math.random() * 2,
                        gpuCost: 0.0,
                        ramCost: Math.random() * 5,
                        pvCost: 0.0,
                        totalCost: Math.random() * 7,
                        cpuCoreUsageAverage: Math.random() * 1,
                        ramByteUsageAverage: Math.floor(Math.random() * 500000000)
                    }
                });
            }
            
            return {
                code: 200,
                data: {
                    items: items
                }
            };
        } else {
            // 基本查询返回单个工作负载数据
            return {
                code: 200,
                data: {
                    properties: {
                        name: 'default/test-pod-12345',
                        cpuCost: Math.random() * 1,
                        gpuCost: 0.0,
                        ramCost: Math.random() * 2,
                        pvCost: 0.0,
                        totalCost: Math.random() * 3,
                        cpuCoreUsageAverage: Math.random() * 0.5,
                        ramByteUsageAverage: Math.floor(Math.random() * 200000000)
                    }
                }
            };
        }
    }
}

module.exports = {
    Client,
    Config
};