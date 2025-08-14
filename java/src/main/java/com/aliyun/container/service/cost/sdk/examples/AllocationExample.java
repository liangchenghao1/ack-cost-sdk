package com.aliyun.container.service.cost.sdk.examples;

import com.aliyun.container.service.cost.sdk.Client;
import com.aliyun.container.service.cost.sdk.Config;
import java.util.Map;
import java.util.HashMap;

public class AllocationExample {
    public static void main(String[] args) {
        try {
            // 创建客户端
            Config config = new Config();
            config.setEndpoint("http://127.0.0.1:8080");
            Client client = new Client(config);

            // 示例: 获取某个DaemonSet昨天分摊集群账单的费用
            System.out.println("=== 获取DaemonSet业务分摊账单 ===");
            example(client);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void example(Client client) {
        try {
            // 获取某个DaemonSet昨天分摊集群账单的费用
            Map<String, Object> request = new HashMap<>();
            request.put("window", "yesterday");
            request.put("filter", "namespace:\"kube-system\"+controllerKind:\"DaemonSet\"+label[app]:\"terway-eniip\"");

            Map<String, Object> response = client.getAllocation(request);
            System.out.println("Response code: " + response.get("code"));
            
            if (response.containsKey("data")) {
                System.out.println("Response data: " + response.get("data"));
            }
        } catch (Exception e) {
            System.err.println("Error in example: " + e.getMessage());
            e.printStackTrace();
        }
    }
}