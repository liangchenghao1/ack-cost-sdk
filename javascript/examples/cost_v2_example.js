const { Client, Config } = require('../src/client');

async function main() {
    try {
        // 创建客户端
        const config = new Config();
        config.endpoint = 'http://127.0.0.1:8080';
        const client = new Client(config);

        // 示例1: 查询DaemonSet昨天的估算成本明细
        console.log('=== 示例1: 查询DaemonSet昨天的估算成本明细 ===');
        await example1(client);

        // 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
        console.log('\n=== 示例2: 获取某个Pod三小时内的估算成本明细 ===');
        await example2(client);

        // 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
        console.log('\n=== 示例3: 按Label聚合应用成本 ===');
        await example3(client);

    } catch (error) {
        console.error('Error:', error.message);
    }
}

async function example1(client) {
    try {
        // 查询DaemonSet昨天的成本
        const request = {
            window: 'yesterday',
            filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
        };

        const response = await client.getCostV2(request);
        console.log('Response code:', response.code);
        
        if (response.data) {
            console.log('Response data:', JSON.stringify(response.data, null, 2));
        }
    } catch (error) {
        console.error('Error in example1:', error.message);
    }
}

async function example2(client) {
    try {
        // 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
        const request = {
            window: '2024-03-24T00:00:00Z,2024-03-24T03:00:00Z',
            step: '1h',
            filter: 'namespace:"kube-system"+pod:"terway-eniip-kz68n"'
        };

        const response = await client.getCostV2(request);
        console.log('Response code:', response.code);
        
        if (response.data) {
            console.log('Response data:', JSON.stringify(response.data, null, 2));
        }
    } catch (error) {
        console.error('Error in example2:', error.message);
    }
}

async function example3(client) {
    try {
        // 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
        const request = {
            window: '2h',
            aggregate: 'label:app',
            idle: 'true'
        };

        const response = await client.getCostV2(request);
        console.log('Response code:', response.code);
        
        if (response.data) {
            console.log('Response data:', JSON.stringify(response.data, null, 2));
        }
    } catch (error) {
        console.error('Error in example3:', error.message);
    }
}

// 运行示例
main();