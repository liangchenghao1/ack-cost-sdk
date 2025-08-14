package com.aliyun.container.service.cost.sdk.examples;

import com.aliyun.container.service.cost.sdk.Client;
import com.aliyun.container.service.cost.sdk.Config;
import com.aliyun.container.service.cost.sdk.model.AllocationRequest;

/**
 * Allocation API 使用示例
 */
public class AllocationExample {
    /**
     * 示例: 获取某个DaemonSet昨天分摊集群账单的费用
     */
    public static void example(Client client) {
        System.out.println("=== 获取DaemonSet业务分摊账单 ===");

        AllocationRequest request = new AllocationRequest()
                .setWindow("yesterday")
                .setFilter("namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");

        try {
            // 注意：由于数据模型转换未完全实现，这里仅演示调用方式
            // client.getAllocation().getAllocation(request);
            System.out.println("请求已发送: " + request.getWindow() + ", " + request.getFilter());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 创建客户端
        Config config = new Config()
                .setApiServer("https://kubernetes.default.svc")
                .setRetryCount(3)
                .setRetryWait(2);

        Client client = new Client(config);

        // 示例: 获取某个DaemonSet昨天分摊集群账单的费用
        example(client);
    }
}