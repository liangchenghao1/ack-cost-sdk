# Java SDK for ACK Cost API

阿里云ACK Cost API的Java语言SDK实现。

## 安装

### 方式1：复制源码到项目

将 `src/main/java/org/openapitools/client/` 目录下的所有Java文件复制到您的项目中。

### 方式2：构建JAR包

```bash
# 构建JAR包
mvn clean package

# 将生成的JAR包添加到项目依赖
# target/openapi-java-client-2.0.0.jar
# target/dependency/*.jar
```

## 快速开始

```java
package examples;

import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.AllocationSetRange;

public class CostQueryExample {
    
    public static void main(String[] args) {
        // 创建客户端
        String proxyPath = "/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy";
        ApiClient apiClient = Configuration.newApiClientWithKubeconfigAndProxy(null, proxyPath);
        
        System.out.println("Client created successfully!");
        System.out.println("Server URL: " + apiClient.getBasePath());
        
        // 创建API实例
        DefaultApi defaultApi = new DefaultApi(apiClient);
        
        // 查询成本数据
        try {
            AllocationSetRange response = defaultApi.getCost(
                "1h",  // window
                "namespace:\"kube-system\"+controllerKind:\"ReplicaSet\"+label[app]:\"ack-cost-exporter\"",  // filter
                null, null, null, null, null, null, null  // other parameters
            );
            
            // 显示结果
            if (response != null && response.getData() != null) {
                System.out.println("Found " + response.getData().size() + " time range data entries");
                for (int i = 0; i < response.getData().size(); i++) {
                    System.out.println("\nTime Range " + (i + 1) + ":");
                    var timeRangeData = response.getData().get(i);
                    
                    for (var entry : timeRangeData.entrySet()) {
                        String resourceName = entry.getKey();
                        var allocation = entry.getValue();
                        
                        System.out.println("  Pod: " + resourceName);
                        if (allocation.getCost() != null) {
                            System.out.println("    Cost: " + String.format("%.4f", allocation.getCost()));
                        }
                        if (allocation.getCpuCoreRequestAverage() != null) {
                            System.out.println("    CPU: " + String.format("%.2f", allocation.getCpuCoreRequestAverage()) + " cores");
                        }
                        if (allocation.getRamByteRequestAverage() != null) {
                            System.out.println("    RAM: " + String.format("%.2f", allocation.getRamByteRequestAverage() / (1024.0 * 1024.0)) + " MB");
                        }
                    }
                }
            }
            
        } catch (ApiException e) {
            System.err.println("API call failed: " + e.getMessage());
            System.err.println("Status code: " + e.getCode());
        }
    }
}
```

## API

| 方法 | 描述                             |
|------|--------------------------------|
| `getCost` | 调用Cost API，用于查询实时估算成本          |
| `getAllocation` | 调用Allocation API，用于查询业务分摊账单的成本 |

## 客户端创建

```java
// 使用默认 kubeconfig
ApiClient apiClient = Configuration.newApiClientWithKubeconfigAndProxy(null, proxyPath);

// 使用指定 kubeconfig
ApiClient apiClient = Configuration.newApiClientWithKubeconfigAndProxy("/path/to/kubeconfig", proxyPath);

// 直接 API 访问
ApiClient apiClient = new ApiClient();
apiClient.setBasePath("https://your-api-server.com");
```

## 示例

```bash
# 编译项目
mvn clean compile

# 运行示例
mvn exec:java -Dexec.mainClass="examples.CostQueryExample"
```

## 依赖

- Java 8+
- Maven 3.6+
- 相关依赖请参考 `pom.xml`