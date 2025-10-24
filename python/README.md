# Python SDK for ACK Cost API

阿里云ACK Cost API的Python语言SDK实现。

## 安装

```bash
pip install git+https://github.com/AliyunContainerService/cost-sdk.git#subdirectory=python
```


## 快速开始

```python
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from openapi_client import ApiClient, DefaultApi

def main():
    # 创建客户端
    proxy_path = "/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy"
    api_client = ApiClient.new_api_client_with_kubeconfig_and_proxy(proxy_path=proxy_path)
    
    print("Client created successfully!")
    print(f"Server URL: {api_client.configuration.host}")
    
    # 创建API实例
    default_api = DefaultApi(api_client)
    
    # 查询成本数据
    try:
        response = default_api.get_cost(
            window="1h",
            filter='namespace:"kube-system"+controllerKind:"ReplicaSet"+label[app]:"ack-cost-exporter"'
        )
        
        # 显示结果
        if response and response.data:
            print(f"Found {len(response.data)} time range data entries")
            for i, time_range_data in enumerate(response.data):
                print(f"\nTime Range {i+1}:")
                for resource_name, allocation in time_range_data.items():
                    print(f"  Pod: {resource_name}")
                    if allocation.cost is not None:
                        print(f"    Cost: {allocation.cost:.4f}")
                    if allocation.cpu_core_request_average is not None:
                        print(f"    CPU: {allocation.cpu_core_request_average:.2f} cores")
                    if allocation.ram_byte_request_average is not None:
                        print(f"    RAM: {allocation.ram_byte_request_average / (1024*1024):.2f} MB")
                        
    except Exception as e:
        print(f"API call failed: {e}")

if __name__ == "__main__":
    main()
```

## API

| 方法 | 描述                             |
|------|--------------------------------|
| `get_cost` | 调用Cost API，用于查询实时估算成本          |
| `get_allocation` | 调用Allocation API，用于查询业务分摊账单的成本 |

## 客户端创建

```python
# 使用默认 kubeconfig
api_client = ApiClient.new_api_client_with_kubeconfig_and_proxy(proxy_path=proxy_path)

# 使用指定 kubeconfig
api_client = ApiClient.new_api_client_with_kubeconfig_and_proxy(
    kubeconfig_path="/path/to/kubeconfig", 
    proxy_path=proxy_path
)

# 直接 API 访问
from openapi_client import Configuration
configuration = Configuration(host="https://your-api-server.com")
api_client = ApiClient(configuration)
```

## 示例

```bash
cd examples
python cost_query_example.py
```