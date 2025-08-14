# Java SDK for ACK Cost API

阿里云ACK成本API的Java语言SDK实现。

## 安装

### Maven

在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.aliyun.container.service</groupId>
    <artifactId>ack-cost-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

在 `build.gradle` 中添加以下依赖：

```gradle
implementation 'com.aliyun.container.service:ack-cost-sdk:1.0.0'
```

## 功能特性

- 支持所有三种成本API:
  - Cost API (旧版)
  - Cost V2 API (推荐)
  - Allocation API (业务分摊账单)
- 同步和异步调用
- 自动重试机制
- 详细的错误处理
- 符合Java社区最佳实践

## 快速开始

### 创建客户端

```java
import com.aliyun.container.service.cost.sdk.Client;
import com.aliyun.container.service.cost.sdk.Config;

// 创建客户端
Config config = new Config()
    .setApiServer("https://kubernetes.default.svc")
    .setRetryCount(3)
    .setRetryWait(2);

Client client = new Client(config);
```

### 调用Cost V2 API

```java
import com.aliyun.container.service.cost.sdk.model.*;

// 查询DaemonSet昨天的成本
try {
    CostV2Request request = new CostV2Request()
        .setWindow("yesterday")
        .setFilter("namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");
    
    CostV2Response response = client.getCostV2().getCostV2(request);
    
    for (Map<String, CostV2Data> data : response.getData()) {
        for (Map.Entry<String, CostV2Data> entry : data.entrySet()) {
            String name = entry.getKey();
            CostV2Data cost = entry.getValue();
            System.out.println("Name: " + name + ", Cost: " + cost.getCost());
        }
    }
} catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
}
```

### 调用Allocation API

```java
// 查询业务分摊账单
try {
    AllocationRequest request = new AllocationRequest()
        .setWindow("yesterday")
        .setFilter("namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");
    
    AllocationResponse response = client.getAllocation().getAllocation(request);
    
    for (Map<String, AllocationData> data : response.getData()) {
        for (Map.Entry<String, AllocationData> entry : data.entrySet()) {
            String name = entry.getKey();
            AllocationData allocation = entry.getValue();
            System.out.println("Name: " + name + ", Cost: " + allocation.getCost());
        }
    }
} catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
}
```

## API参考

详细API文档请参考[API文档](./API.md)

## 示例

更多示例请参考[examples](./examples/)目录