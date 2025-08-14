/**
 * Cost V2 API 使用示例
 */

const { Client, Config, CostV2Request } = require('../src');

/**
 * 示例1: 查询DaemonSet昨天的估算成本明细
 */
async function example1(client) {
    console.log('=== 示例1: 查询DaemonSet昨天的估算成本明细 ===');
    
    const request = new CostV2Request({
        window: "yesterday",
        filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
    });
    
    try {
        const response = await client.costV2.getCostV2(request);
        
        response.data.forEach(data => {
            Object.entries(data).forEach(([name, cost]) => {
                console.log(`Name: ${name}, Cost: ${cost.cost.toFixed(3)}, CPU Request: ${cost.cpuCoreRequestAverage.toFixed(2)}, Memory Request: ${cost.ramByteRequestAverage.toFixed(0)}`);
            });
        });
    } catch (error) {
        console.error('Error:', error.message);
    }
}

/**
 * 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
 */
async function example2(client) {
    console.log('\n=== 示例2: 获取某个Pod三小时内的估算成本明细 ===');
    
    const request = new CostV2Request({
        window: "2024-03-24T00:00:00Z,2024-03-24T03:00:00Z",
        step: "1h",
        filter: 'namespace:"kube-system"+pod:"terway-eniip-kz68n"'
    });
    
    try {
        const response = await client.costV2.getCostV2(request);
        
        response.data.forEach(data => {
            Object.entries(data).forEach(([name, cost]) => {
                console.log(`Name: ${name}, Period: ${cost.start} - ${cost.end}, Cost: ${cost.cost.toFixed(3)}`);
            });
        });
    } catch (error) {
        console.error('Error:', error.message);
    }
}

/**
 * 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
 */
async function example3(client) {
    console.log('\n=== 示例3: 按Label聚合应用成本 ===');
    
    const request = new CostV2Request({
        window: "2h",
        aggregate: "label:app",
        idle: true
    });
    
    try {
        const response = await client.costV2.getCostV2(request);
        
        response.data.forEach(data => {
            Object.entries(data).forEach(([name, cost]) => {
                console.log(`App: ${name}, Cost: ${cost.cost.toFixed(3)}, CPU Usage: ${cost.cpuCoreUsageAverage.toFixed(3)}, Memory Usage: ${cost.ramByteUsageAverage.toFixed(0)}`);
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

    // 示例1: 查询DaemonSet昨天的估算成本明细
    await example1(client);

    // 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
    await example2(client);

    // 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
    await example3(client);
}

main().catch(console.error);