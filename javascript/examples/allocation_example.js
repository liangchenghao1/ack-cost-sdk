const { Client, Config } = require('../src/client');

async function main() {
    try {
        // 创建客户端
        const config = new Config();
        config.endpoint = 'http://127.0.0.1:8080';
        const client = new Client(config);

        // 示例: 获取某个DaemonSet昨天分摊集群账单的费用
        console.log('=== 获取DaemonSet业务分摊账单 ===');
        await example(client);

    } catch (error) {
        console.error('Error:', error.message);
    }
}

async function example(client) {
    try {
        // 获取某个DaemonSet昨天分摊集群账单的费用
        const request = {
            window: 'yesterday',
            filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
        };

        const response = await client.getAllocation(request);
        console.log('Response code:', response.code);
        
        if (response.data) {
            console.log('Response data:', JSON.stringify(response.data, null, 2));
        }
    } catch (error) {
        console.error('Error in example:', error.message);
    }
}

// 运行示例
main();