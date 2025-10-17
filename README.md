# ACK Cost API SDK

阿里云ACK Cost API的SDK实现，支持通过kubeconfig进行身份验证，查询Kubernetes集群中的成本分配数据。

## 支持的语言

| 语言 | 文档 | 示例 |
|------|------|------|
| **Go** | [README](go/README.md) | [cost_query_example.go](go/examples/cost_query_example.go) |
| **Python** | [README](python/README.md) | [cost_query_example.py](python/examples/cost_query_example.py) |
| **Java** | [README](java/README.md) | [CostQueryExample.java](java/examples/CostQueryExample.java) |
| **JavaScript** | [README](javascript/README.md) | [cost_query_example.js](javascript/examples/cost_query_example.js) |

## API 

| API             | 描述 |
|-----------------|------|
| `Cost API`       | 用于查询实时估算成本 |
| `Allocation API` | 用于查询业务分摊账单的成本 |


## 前置条件

- 集群kubeconfig文件（默认`~/.kube/config`或通过`KUBECONFIG`环境变量指定）
- 集群已部署 Cost API服务

## License

Apache 2.0 License

