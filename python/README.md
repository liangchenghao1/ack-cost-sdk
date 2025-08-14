# 阿里云ACK成本管理Python SDK

阿里云容器服务Kubernetes版(ACK)成本管理Python SDK，用于查询Kubernetes集群的成本数据。

## 功能特性

- 支持Cost API、Cost V2 API和Allocation API
- 同步和异步调用支持
- 自动重试机制
- 完善的错误处理
- 符合Python社区最佳实践

## 安装

```bash
pip install ack-cost-sdk
```

或者从源码安装：

```bash
git clone https://github.com/AliyunContainerService/cost-sdk.git
cd cost-sdk/python
pip install -r requirements.txt
```

## 快速开始

### 创建客户端

```python
from ack_cost_sdk import Client

# 创建客户端
client = Client(
    api_server="https://kubernetes.default.svc",
    retry_count=3,
    retry_wait=2
)
```

### 查询Cost V2数据

```python
# 查询DaemonSet昨天的成本
response = client.cost_v2.get_cost_v2(
    window="yesterday",
    filter='namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
)

for data in response.data:
    for name, cost in data.items():
        print(f"Name: {name}, Cost: {cost.cost}")
```

### 查询Allocation数据

```python
# 查询业务分摊账单
response = client.allocation.get_allocation(
    window="yesterday",
    filter='namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
)

for data in response.data:
    for name, allocation in data.items():
        print(f"Name: {name}, Allocation Cost: {allocation.cost}")
```

## API参考

详细API文档请参考[官方文档](https://help.aliyun.com/zh/ack/ack-managed-and-ack-dedicated/user-guide/overview-of-calling-an-api-to-query-cost-insights-data).

## 常见问题

请参考项目根目录下的[FAQ.md](../FAQ.md)文件。