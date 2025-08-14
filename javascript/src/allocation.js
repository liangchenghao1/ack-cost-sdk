/**
 * Allocation API服务模块
 * @module allocation
 */

const { httpErrorFromStatusCode } = require('./errors');

/**
 * Allocation API请求参数类
 */
class AllocationRequest {
    /**
     * 构造函数
     * @param {Object} options - 请求参数
     * @param {string} options.window - 查询的持续时间
     * @param {string} [options.filter] - 资源过滤条件
     * @param {string} [options.step] - 时间分段
     * @param {string} [options.aggregate] - 聚合维度
     * @param {boolean} [options.idle] - 是否展示闲置成本
     * @param {boolean} [options.shareIdle] - 是否分摊闲置成本
     * @param {string} [options.shareSplit] - 闲置分摊策略
     * @param {boolean} [options.idleByNode] - 是否按节点维度聚合闲置成本
     * @param {string} [options.targetType] - 成本分摊的目标类型
     * @param {string} [options.format] - 成本导出格式
     */
    constructor(options) {
        this.window = options.window;
        this.filter = options.filter;
        this.step = options.step;
        this.aggregate = options.aggregate;
        this.idle = options.idle;
        this.shareIdle = options.shareIdle;
        this.shareSplit = options.shareSplit;
        this.idleByNode = options.idleByNode;
        this.targetType = options.targetType;
        this.format = options.format;
    }
}

/**
 * Kubernetes对象属性类
 */
class AllocationProperties {
    /**
     * 构造函数
     * @param {Object} options - 属性选项
     * @param {string} [options.pod] - Pod名称
     * @param {string} [options.node] - 节点名称
     * @param {string} [options.namespace] - 命名空间
     * @param {string} [options.controllerKind] - 控制器类型
     * @param {string} [options.controller] - 控制器名称
     * @param {string} [options.providerID] - 节点对应ECS实例ID
     * @param {Object} [options.labels] - Pod标签
     */
    constructor(options = {}) {
        this.pod = options.pod;
        this.node = options.node;
        this.namespace = options.namespace;
        this.controllerKind = options.controllerKind;
        this.controller = options.controller;
        this.providerID = options.providerID;
        this.labels = options.labels || {};
    }
}

/**
 * Allocation 成本数据类
 */
class AllocationData {
    /**
     * 构造函数
     * @param {Object} options - 数据选项
     * @param {string} [options.name] - 名称
     * @param {AllocationProperties} [options.properties] - 属性
     * @param {string} [options.start] - 开始时间
     * @param {string} [options.end] - 结束时间
     * @param {number} [options.cpuCoreRequestAverage] - 请求的平均CPU核心数
     * @param {number} [options.cpuCoreUsageAverage] - 使用的平均CPU核心数
     * @param {number} [options.ramByteRequestAverage] - 请求的平均内存量
     * @param {number} [options.ramByteUsageAverage] - 使用的平均内存量
     * @param {number} [options.cost] - 分摊成本
     * @param {number} [options.costRatio] - 成本占比
     * @param {number} [options.customCost] - 自定义成本
     */
    constructor(options = {}) {
        this.name = options.name || "";
        this.properties = options.properties || new AllocationProperties();
        this.start = options.start || "";
        this.end = options.end || "";
        this.cpuCoreRequestAverage = options.cpuCoreRequestAverage || 0.0;
        this.cpuCoreUsageAverage = options.cpuCoreUsageAverage || 0.0;
        this.ramByteRequestAverage = options.ramByteRequestAverage || 0.0;
        this.ramByteUsageAverage = options.ramByteUsageAverage || 0.0;
        this.cost = options.cost || 0.0;
        this.costRatio = options.costRatio || 0.0;
        this.customCost = options.customCost || 0.0;
    }
}

/**
 * Allocation API响应类
 */
class AllocationResponse {
    /**
     * 构造函数
     * @param {Object} options - 响应选项
     * @param {Array<Object>} [options.data] - 数据数组
     */
    constructor(options = {}) {
        this.data = options.data || [];
    }
}

/**
 * Allocation API服务类
 */
class AllocationService {
    /**
     * 构造函数
     * @param {Client} client - SDK客户端
     */
    constructor(client) {
        this.client = client;
    }
    
    /**
     * 查询Allocation成本数据
     * @param {AllocationRequest} request - 请求参数
     * @returns {Promise<AllocationResponse>} 响应数据
     */
    async getAllocation(request) {
        // 构建URL
        let apiURL = `${this.client.config.apiServer}/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/v2/allocation`;
        
        // 添加查询参数
        const params = new URLSearchParams();
        params.append('window', request.window);
        
        if (request.filter) {
            params.append('filter', request.filter);
        }
        
        if (request.step) {
            params.append('step', request.step);
        }
        
        if (request.aggregate) {
            params.append('aggregate', request.aggregate);
        }
        
        if (request.idle !== undefined) {
            params.append('idle', request.idle.toString());
        }
        
        if (request.shareIdle !== undefined) {
            params.append('shareIdle', request.shareIdle.toString());
        }
        
        if (request.shareSplit) {
            params.append('shareSplit', request.shareSplit);
        }
        
        if (request.idleByNode !== undefined) {
            params.append('idleByNode', request.idleByNode.toString());
        }
        
        if (request.targetType) {
            params.append('targetType', request.targetType);
        }
        
        if (request.format) {
            params.append('format', request.format);
        }
        
        // 构建完整URL
        if (params.toString()) {
            apiURL = `${apiURL}?${params.toString()}`;
        }
        
        try {
            // 发送请求
            const response = await this.client.doRequest({
                method: 'GET',
                url: apiURL
            });
            
            // 检查响应状态码
            if (response.status !== 200) {
                throw httpErrorFromStatusCode(response.status);
            }
            
            // 解析响应
            const rawData = response.data;
            const result = new AllocationResponse();
            
            // 转换数据格式
            result.data = (rawData.data || []).map(item => {
                const dataDict = {};
                for (const [key, value] of Object.entries(item)) {
                    const allocationData = new AllocationData({
                        name: value.name,
                        properties: new AllocationProperties({
                            pod: value.properties?.pod,
                            node: value.properties?.node,
                            namespace: value.properties?.namespace,
                            controllerKind: value.properties?.controllerKind,
                            controller: value.properties?.controller,
                            providerID: value.properties?.providerID,
                            labels: value.properties?.labels || {}
                        }),
                        start: value.start,
                        end: value.end,
                        cpuCoreRequestAverage: value.cpuCoreRequestAverage,
                        cpuCoreUsageAverage: value.cpuCoreUsageAverage,
                        ramByteRequestAverage: value.ramByteRequestAverage,
                        ramByteUsageAverage: value.ramByteUsageAverage,
                        cost: value.cost,
                        costRatio: value.costRatio,
                        customCost: value.customCost
                    });
                    dataDict[key] = allocationData;
                }
                return dataDict;
            });
            
            return result;
        } catch (error) {
            if (error instanceof httpErrorFromStatusCode().constructor) {
                throw error;
            }
            throw new Error(`Failed to send request: ${error.message}`);
        }
    }
}

module.exports = {
    AllocationService,
    AllocationRequest,
    AllocationProperties,
    AllocationData,
    AllocationResponse
};