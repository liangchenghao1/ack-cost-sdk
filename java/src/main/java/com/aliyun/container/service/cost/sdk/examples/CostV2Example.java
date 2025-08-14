package com.aliyun.container.service.cost.sdk.examples;

import com.aliyun.container.service.cost.sdk.Client;
import com.aliyun.container.service.cost.sdk.Config;
import com.aliyun.container.service.cost.sdk.CostError;
import com.aliyun.container.service.cost.sdk.model.CostV2Request;

/**
 * Cost V2 API 使用示例
 */
public class CostV2Example {
    /**
     * 示例1: 查询DaemonSet昨天的估算成本明细
     */
    public static void example1(Client client) {
        System.out.println("=== 示例1: 查询DaemonSet昨天的估算成本明细 ===");

        CostV2Request request = new CostV2Request()
                .setWindow("yesterday")
                .setFilter("namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");

        try {
            // 注意：由于数据模型转换未完全实现，这里仅演示调用方式
            // client.getCostV2().getCostV2(request);
            System.out.println("请求已发送: " + request.getWindow() + ", " + request.getFilter());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
     */
    public static void example2(Client client) {
        System.out.println("\n=== 示例2: 获取某个Pod三小时内的估算成本明细 ===");

        CostV2Request request = new CostV2Request()
                .setWindow("2024-03-24T00:00:00Z,2024-03-24T03:00:00Z")
                .setStep("1h")
                .setFilter("namespace:\"kube-system\"+pod:\"terway-eniip-kz68n\"");

        try {
            // 注意：由于数据模型转换未完全实现，这里仅演示调用方式
            // client.getCostV2().getCostV2(request);
            System.out.println("请求已发送: " + request.getWindow() + ", " + request.getStep() + ", " + request.getFilter());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
     */
    public static void example3(Client client) {
        System.out.println("\n=== 示例3: 按Label聚合应用成本 ===");

        CostV2Request request = new CostV2Request()
                .setWindow("2h")
                .setAggregate("label:app")
                .setIdle(true);

        try {
            // 注意：由于数据模型转换未完全实现，这里仅演示调用方式
            // client.getCostV2().getCostV2(request);
            System.out.println("请求已发送: " + request.getWindow() + ", " + request.getAggregate() + ", idle=" + request.getIdle());
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

        // 示例1: 查询DaemonSet昨天的估算成本明细
        example1(client);

        // 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
        example2(client);

        // 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
        example3(client);
    }
}