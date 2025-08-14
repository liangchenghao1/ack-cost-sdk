# JavaScript SDK for ACK Cost API

阿里云ACK成本API的JavaScript语言SDK实现。

## 安装

```bash
npm install ack-cost-sdk
```

或者使用 yarn：

```bash
yarn add ack-cost-sdk
```

## 功能特性

- 支持所有三种成本API:
  - Cost API (旧版)
  - Cost V2 API (推荐)
  - Allocation API (业务分摊账单)
- 支持Promise和async/await
- 自动重试机制
- 详细的错误处理
- 符合JavaScript/Node.js社区最佳实践

## 快速开始

### 创建客户端

```javascript
const { Client, Config } = require('ack-cost-sdk');

// 创建客户端
const client = new Client(new Config({
    apiServer: "https://kubernetes.default.svc",
    retryCount: 3,
    retryWait: 2
}));
```

### 调用Cost V2 API

```javascript
// 查询DaemonSet昨天的成本
async function example() {
    try {
        const response = await client.costV2.getCostV2({
            window: "yesterday",
            filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
        });

        response.data.forEach(data => {
            Object.entries(data).forEach(([name, cost]) => {
                console.log(`Name: ${name}, Cost: ${cost.cost}`);
            });
        });
    } catch (error) {
        console.error('Error:', error);
    }
}
```

### 调用Allocation API

```javascript
// 查询业务分摊账单
async function example() {
    try {
        const response = await client.allocation.getAllocation({
            window: "yesterday",
            filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
        });

        response.data.forEach(data => {
            Object.entries(data).forEach(([name, allocation]) => {
                console.log(`Name: ${name}, Cost: ${allocation.cost}`);
            });
        });
    } catch (error) {
        console.error('Error:', error);
    }
}
```

## API参考

详细API文档请参考[API文档](./API.md)

## 示例

更多示例请参考[examples](./examples/)目录