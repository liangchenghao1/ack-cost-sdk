/**
 * Allocation API 使用示例
 */

const { Client, Config, AllocationRequest } = require('../src');

/**
 * 示例: 获取某个DaemonSet昨天分摊集群账单的费用
 */
async function example(client) {
    console.log('=== 获取DaemonSet业务分摊账单 ===');
    
    const request = new AllocationRequest({
        window: "yesterday",
        filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
    });
    
    try {
        const response = await client.allocation.getAllocation(request);
        
        response.data.forEach(data => {
            Object.entries(data).forEach(([name, allocation]) => {
                console.log(`Name: ${name}, Allocation Cost: ${allocation.cost.toFixed(3)}, CPU Request: ${allocation.cpuCoreRequestAverage.toFixed(2)}, Memory Request: ${allocation.ramByteRequestAverage.toFixed(0)}`);
            });
        });
    } catch (error) {
        console.error('Error:', error.message);
    }
}

async function main() {
    // 创建客户端
    const client = new Client(new Config({
        apiServer: "https://kubernetes.default.svc",
        retryCount: 3,
        retryWait: 2
    }));

    // 示例: 获取某个DaemonSet昨天分摊集群账单的费用
    await example(client);
}

main().catch(console.error);