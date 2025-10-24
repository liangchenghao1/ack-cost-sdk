# JavaScript SDK for ACK Cost API

阿里云ACK Cost API的JavaScript语言SDK实现。

## 安装

### 方式1：复制源码到项目

将 `src/` 目录下的所有JavaScript文件复制到您的项目中。

### 方式2：本地安装

```bash
# 安装依赖
npm install

# 构建项目
npm run build
```

## 快速开始

```javascript
const { ApiClient, DefaultApi } = require('./dist/index.js');

async function main() {
    // 创建客户端
    const proxyPath = "/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy";
    const apiClient = ApiClient.newApiClientWithKubeconfigAndProxy(null, proxyPath);
    
    console.log("Client created successfully!");
    console.log(`Server URL: ${apiClient.basePath}`);
    
    // 创建API实例
    const defaultApi = new DefaultApi(apiClient);
    
    // 查询成本数据
    try {
        const response = await new Promise((resolve, reject) => {
            defaultApi.getCost("1h", {
                filter: 'namespace:"kube-system"+controllerKind:"ReplicaSet"+label[app]:"ack-cost-exporter"'
            }, (error, data, response) => {
                if (error) {
                    reject(error);
                } else {
                    resolve(data);
                }
            });
        });
        
        // 显示结果
        if (response && response.data) {
            console.log(`Found ${response.data.length} time range data entries`);
            for (let i = 0; i < response.data.length; i++) {
                console.log(`\nTime Range ${i + 1}:`);
                const dataItem = response.data[i];
                
                for (const [resourceName, allocation] of Object.entries(dataItem)) {
                    console.log(`  Pod: ${resourceName}`);
                    if (allocation.cost !== undefined) {
                        console.log(`    Cost: ${allocation.cost.toFixed(4)}`);
                    }
                    if (allocation.cpuCoreRequestAverage !== undefined) {
                        console.log(`    CPU: ${allocation.cpuCoreRequestAverage.toFixed(2)} cores`);
                    }
                    if (allocation.ramByteRequestAverage !== undefined) {
                        console.log(`    RAM: ${(allocation.ramByteRequestAverage / (1024 * 1024)).toFixed(2)} MB`);
                    }
                }
            }
        }
        
    } catch (error) {
        console.error("API call failed:", error.message);
    }
}

main();
```

## API

| 方法 | 描述                             |
|------|--------------------------------|
| `getCost` | 调用Cost API，用于查询实时估算成本          |
| `getAllocation` | 调用Allocation API，用于查询业务分摊账单的成本 |

## 客户端创建

```javascript
// 使用默认 kubeconfig
const apiClient = ApiClient.newApiClientWithKubeconfigAndProxy(null, proxyPath);

// 使用指定 kubeconfig
const apiClient = ApiClient.newApiClientWithKubeconfigAndProxy("/path/to/kubeconfig", proxyPath);

// 直接 API 访问
const apiClient = new ApiClient();
apiClient.basePath = "https://your-api-server.com";
```

## 示例

```bash
# 安装依赖
npm install

# 构建项目
npm run build

# 运行示例
cd examples
node cost_query_example.js
```

## 依赖

- Node.js 14+
- npm 6+