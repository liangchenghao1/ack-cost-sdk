# JavaScript SDK for ACK Cost

阿里云容器服务 ACK 成本 JavaScript SDK，用于访问 ACK 成本相关 API。

## 功能

- 成本 V2 API 访问
- Allocation API 访问
- 配置管理

## 安装

```bash
npm install
```

## 使用示例

### 创建客户端

```javascript
const { Client, Config } = require('./src/client');

const config = new Config();
config.endpoint = 'http://127.0.0.1:8080';
const client = new Client(config);
```

### 获取成本数据

```javascript
const request = {
    window: 'yesterday',
    filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
};

const response = await client.getCostV2(request);
```

### 获取分摊数据

```javascript
const request = {
    window: 'yesterday',
    filter: 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
};

const response = await client.getAllocation(request);
```

## 示例代码

详细的示例代码请参考 [examples](examples/) 目录：

- [cost_v2_example.js](examples/cost_v2_example.js) - Cost V2 API 使用示例
- [allocation_example.js](examples/allocation_example.js) - Allocation API 使用示例

运行示例：

```bash
node examples/cost_v2_example.js
```

## 测试

运行单元测试：

```bash
make test-javascript
```

或

```bash
npm test
```