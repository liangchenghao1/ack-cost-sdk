package com.aliyun.container.service.cost.sdk.examples;

import com.aliyun.container.service.cost.sdk.Client;
import com.aliyun.container.service.cost.sdk.Config;
import java.util.Map;
import java.util.HashMap;

public class CostV2Example {
    public static void main(String[] args) {
        try {
            // 创建客户端
            Config config = new Config();
            config.setEndpoint("http://127.0.0.1:8080");
            Client client = new Client(config);

            // 示例1: 查询DaemonSet昨天的估算成本明细
            System.out.println("=== 示例1: 查询DaemonSet昨天的估算成本明细 ===");
            example1(client);

            // 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
            System.out.println("\n=== 示例2: 获取某个Pod三小时内的估算成本明细 ===");
            example2(client);

            // 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
            System.out.println("\n=== 示例3: 按Label聚合应用成本 ===");
            example3(client);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void example1(Client client) {
        try {
            // 查询DaemonSet昨天的成本
            Map<String, Object> request = new HashMap<>();
            request.put("window", "yesterday");
            request.put("filter", "namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");

            Map<String, Object> response = client.getCostV2(request);
            System.out.println("Response code: " + response.get("code"));
            
            if (response.containsKey("data")) {
                System.out.println("Response data: " + response.get("data"));
            }
        } catch (Exception e) {
            System.err.println("Error in example1: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void example2(Client client) {
        try {
            // 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
            Map<String, Object> request = new HashMap<>();
            request.put("window", "2024-03-24T00:00:00Z,2024-03-24T03:00:00Z");
            request.put("step", "1h");
            request.put("filter", "namespace:\"kube-system\"+pod:\"terway-eniip-kz68n\"");

            Map<String, Object> response = client.getCostV2(request);
            System.out.println("Response code: " + response.get("code"));
            
            if (response.containsKey("data")) {
                System.out.println("Response data: " + response.get("data"));
            }
        } catch (Exception e) {
            System.err.println("Error in example2: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void example3(Client client) {
        try {
            // 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
            Map<String, Object> request = new HashMap<>();
            request.put("window", "2h");
            request.put("aggregate", "label:app");
            request.put("idle", "true");

            Map<String, Object> response = client.getCostV2(request);
            System.out.println("Response code: " + response.get("code"));
            
            if (response.containsKey("data")) {
                System.out.println("Response data: " + response.get("data"));
            }
        } catch (Exception e) {
            System.err.println("Error in example3: " + e.getMessage());
            e.printStackTrace();
        }
    }
}