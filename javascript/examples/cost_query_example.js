/**
 * Cost Query Example
 * 使用kubeconfig进行身份验证，查询Kubernetes集群中的成本数据
 */

const { ApiClient, DefaultApi } = require('../dist/index.js');
const fs = require('fs');
const path = require('path');

/**
 * 查找kubeconfig文件
 * @returns {string|null} kubeconfig文件路径
 */
function findKubeconfigFile() {
    // 尝试KUBECONFIG环境变量
    const kubeconfigEnv = process.env.KUBECONFIG;
    if (kubeconfigEnv && fs.existsSync(kubeconfigEnv)) {
        return kubeconfigEnv;
    }

    // 尝试默认路径
    const homeDir = process.env.HOME || process.env.USERPROFILE;
    const defaultPath = path.join(homeDir, '.kube', 'config');
    if (fs.existsSync(defaultPath)) {
        return defaultPath;
    }
    
    return null;
}

/**
 * 查询成本数据
 */
async function queryCostData() {
    console.log('============================================================');
    console.log('JavaScript SDK Kubeconfig Cost Query Example');
    console.log('============================================================');

    const kubeconfigPath = findKubeconfigFile();
    if (!kubeconfigPath) {
        console.error('No kubeconfig file found. Please ensure KUBECONFIG environment variable is set or file exists at ~/.kube/config');
        process.exit(1);
    }
    console.log(`Found kubeconfig file: ${kubeconfigPath}`);

    console.log('Starting cost data query...');
    try {
        // 1. 使用kubeconfig和代理创建API客户端
        // 代理路径与kubectl命令中的路径一致
        const proxyPath = '/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy';
        const apiClient = ApiClient.newApiClientWithKubeconfigAndProxy(
            kubeconfigPath,
            proxyPath
        );
        console.log('Client created successfully!');
        console.log(`Server URL: ${apiClient.basePath}`);

        // 2. 创建API实例
        const defaultApi = new DefaultApi(apiClient);
        console.log('API instance created successfully');

        // 3. 定义查询参数
        const window = '1h';
        const filter = 'namespace:"kube-system"+controllerKind:"ReplicaSet"+label[app]:"ack-cost-exporter"';
        
        console.log('Querying cost data for ack-cost-exporter in kube-system namespace...');
        console.log(`Request URL: ${apiClient.basePath}/v2/cost?window=${window}&filter=${encodeURIComponent(filter)}`);

        // 4. 发送API请求
        const opts = {
            filter: filter
        };
        
        const response = await new Promise((resolve, reject) => {
            defaultApi.getCost(window, opts, (error, data, response) => {
                if (error) {
                    reject(error);
                } else {
                    resolve(data);
                }
            });
        });

        console.log('Successfully retrieved cost data');
        console.log('Response data structure:');

        if (response && response.data) {
            console.log(`   - Number of data entries: ${response.data.length}`);
            
            // Display each time range's data
            for (let i = 0; i < response.data.length; i++) {
                const dataItem = response.data[i];
                console.log(`\n   Time Range ${i + 1}:`);
                
                if (dataItem && typeof dataItem === 'object') {
                    // Display each pod's cost data in this time range
                    for (const [resourceName, allocation] of Object.entries(dataItem)) {
                        if (allocation && typeof allocation === 'object') {
                            console.log(`\n     Pod: ${resourceName}`);
                            
                            // Display basic allocation info
                            if (allocation.name) {
                                console.log(`       Name: ${allocation.name}`);
                            }
                            if (allocation.start) {
                                console.log(`       Start: ${allocation.start}`);
                            }
                            if (allocation.end) {
                                console.log(`       End: ${allocation.end}`);
                            }
                            
                            // Display resource usage
                            if (allocation.cpuCoreRequestAverage !== undefined) {
                                console.log(`       CPU Request Average: ${allocation.cpuCoreRequestAverage.toFixed(2)} cores`);
                            }
                            if (allocation.cpuCoreUsageAverage !== undefined) {
                                console.log(`       CPU Usage Average: ${allocation.cpuCoreUsageAverage.toFixed(2)} cores`);
                            }
                            if (allocation.ramByteRequestAverage !== undefined) {
                                console.log(`       RAM Request Average: ${(allocation.ramByteRequestAverage / (1024 * 1024)).toFixed(2)} MB`);
                            }
                            if (allocation.ramByteUsageAverage !== undefined) {
                                console.log(`       RAM Usage Average: ${(allocation.ramByteUsageAverage / (1024 * 1024)).toFixed(2)} MB`);
                            }
                            
                            // Display cost information
                            if (allocation.cost !== undefined) {
                                console.log(`       Cost: ${allocation.cost.toFixed(4)}`);
                            }
                            if (allocation.costRatio !== undefined) {
                                console.log(`       Cost Ratio: ${(allocation.costRatio * 100).toFixed(2)}%`);
                            }
                            if (allocation.customCost !== undefined) {
                                console.log(`       Custom Cost: ${allocation.customCost.toFixed(4)}`);
                            }
                            
                            // Display properties if available
                            if (allocation.properties) {
                                console.log(`       Properties:`);
                                if (allocation.properties.namespace) {
                                    console.log(`         Namespace: ${allocation.properties.namespace}`);
                                }
                                if (allocation.properties.node) {
                                    console.log(`         Node: ${allocation.properties.node}`);
                                }
                                if (allocation.properties.controller) {
                                    console.log(`         Controller: ${allocation.properties.controller}`);
                                }
                                if (allocation.properties.controllerKind) {
                                    console.log(`         Controller Kind: ${allocation.properties.controllerKind}`);
                                }
                                if (allocation.properties.cluster) {
                                    console.log(`         Cluster: ${allocation.properties.cluster}`);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            console.log('   - No matching resources found');
        }

    } catch (error) {
        console.error('Query failed:', error.message);
        if (error.response) {
            console.error('HTTP status code:', error.response.status);
            console.error('Response body:', error.response.body);
        }
        process.exit(1);
    } finally {
        console.log('============================================================');
        console.log('Example completed successfully!');
    }
}

// 运行示例
if (require.main === module) {
    queryCostData().catch(error => {
        console.error('Example execution failed:', error);
        process.exit(1);
    });
}

module.exports = { queryCostData, findKubeconfigFile };