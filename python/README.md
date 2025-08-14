# Python SDK for ACK Cost

阿里云容器服务 ACK 成本 Python SDK，用于访问 ACK 成本相关 API。

## 功能

- 成本 V2 API 访问
- Allocation API 访问
- 配置管理

## 安装

```bash
# 创建虚拟环境
python3 -m venv venv

# 激活虚拟环境
source venv/bin/activate

# 安装依赖
pip install -r requirements.txt
```

## 使用示例

### 创建客户端

```python
from ack_cost_sdk.client import Client, Config

config = Config()
config.endpoint = 'http://127.0.0.1:8080'
client = Client(config)
```

### 获取成本数据

```python
request = {
    'window': 'yesterday',
    'filter': 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
}

response = client.get_cost_v2(request)
```

### 获取分摊数据

```python
request = {
    'window': 'yesterday',
    'filter': 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
}

response = client.get_allocation(request)
```

## 示例代码

详细的示例代码请参考 [examples](examples/) 目录：

- [cost_v2_example.py](examples/cost_v2_example.py) - Cost V2 API 使用示例
- [allocation_example.py](examples/allocation_example.py) - Allocation API 使用示例

运行示例：

```bash
# 激活虚拟环境
source venv/bin/activate

# 运行示例
PYTHONPATH=. python examples/cost_v2_example.py
```

## 测试

运行单元测试：

```bash
make test-python
```