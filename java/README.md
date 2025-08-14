# Java SDK for ACK Cost

阿里云容器服务 ACK 成本 Java SDK，用于访问 ACK 成本相关 API。

## 功能

- 成本 V2 API 访问
- Allocation API 访问
- 配置管理

## 安装

直接将源代码复制到您的项目中，或打包为 JAR 文件使用。

## 使用示例

### 创建客户端

```java
Config config = new Config();
config.setEndpoint("http://127.0.0.1:8080");
Client client = new Client(config);
```

### 获取成本数据

```java
Map<String, Object> request = new HashMap<>();
request.put("window", "yesterday");
request.put("filter", "namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");

Map<String, Object> response = client.getCostV2(request);
```

### 获取分摊数据

```java
Map<String, Object> request = new HashMap<>();
request.put("window", "yesterday");
request.put("filter", "namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");

Map<String, Object> response = client.getAllocation(request);
```

## 示例代码

详细的示例代码请参考 [examples](src/main/java/com/aliyun/container/service/cost/sdk/examples/) 目录：

- [CostV2Example.java](src/main/java/com/aliyun/container/service/cost/sdk/examples/CostV2Example.java) - Cost V2 API 使用示例
- [AllocationExample.java](src/main/java/com/aliyun/container/service/cost/sdk/examples/AllocationExample.java) - Allocation API 使用示例

运行示例：

```bash
# 编译示例
javac -cp "src/main/java" -d target/classes src/main/java/com/aliyun/container/service/cost/sdk/examples/CostV2Example.java

# 运行示例
java -cp "target/classes:src/main/java" com.aliyun.container.service.cost.sdk.examples.CostV2Example
```

## 测试

运行单元测试：

```bash
make test-java
```