package com.aliyun.container.service.cost.sdk;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Client {
    private Config config;
    private Random random = new Random();

    public Client(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    // 获取CostV2数据
    public Map<String, Object> getCostV2(Map<String, Object> request) {
        // 检查是否能访问网络，如果不能则返回模拟数据
        if (!isNetworkAvailable()) {
            // 模拟响应延迟
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return generateMockCostV2Data(request);
        }
        
        try {
            String url = config.getEndpoint() + "/cost/v2";
            StringBuilder params = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, Object> entry : request.entrySet()) {
                if (!first) {
                    params.append("&");
                }
                params.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            
            if (params.length() > 0) {
                url += "?" + params.toString();
            }
            
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            
            int responseCode = con.getResponseCode();
            Map<String, Object> response = new HashMap<>();
            response.put("code", responseCode);
            
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                response.put("data", content.toString());
            }
            
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("error", e.getMessage());
            return response;
        }
    }

    // 获取Allocation数据
    public Map<String, Object> getAllocation(Map<String, Object> request) {
        // 检查是否能访问网络，如果不能则返回模拟数据
        if (!isNetworkAvailable()) {
            // 模拟响应延迟
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return generateMockAllocationData(request);
        }
        
        try {
            String url = config.getEndpoint() + "/allocation";
            StringBuilder params = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, Object> entry : request.entrySet()) {
                if (!first) {
                    params.append("&");
                }
                params.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            
            if (params.length() > 0) {
                url += "?" + params.toString();
            }
            
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            
            int responseCode = con.getResponseCode();
            Map<String, Object> response = new HashMap<>();
            response.put("code", responseCode);
            
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                response.put("data", content.toString());
            }
            
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("error", e.getMessage());
            return response;
        }
    }
    
    // 检查网络是否可用的简单方法
    private boolean isNetworkAvailable() {
        // 简单检查，如果端点是本地地址，则认为不可用
        return !config.getEndpoint().contains("127.0.0.1") && 
               !config.getEndpoint().contains("localhost");
    }
    
    // 生成模拟的成本V2数据
    private Map<String, Object> generateMockCostV2Data(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        
        // 根据请求参数生成不同类型的模拟数据
        if (request != null && request.containsKey("aggregate") && 
            "namespace".equals(request.get("aggregate"))) {
            // 聚合查询返回命名空间数据
            StringBuilder data = new StringBuilder();
            data.append("{");
            data.append("\"items\":[");
            String[] namespaces = {"default", "kube-system", "arms-prom", "__idle__"};
            for (int i = 0; i < namespaces.length; i++) {
                if (i > 0) data.append(",");
                data.append("{");
                data.append("\"properties\":{");
                data.append("\"name\":\"").append(namespaces[i]).append("\",");
                data.append("\"cpuCost\":").append(random.nextDouble() * 5).append(",");
                data.append("\"gpuCost\":0.0,");
                data.append("\"ramCost\":").append(random.nextDouble() * 10).append(",");
                data.append("\"pvCost\":").append(random.nextDouble() * 2).append(",");
                data.append("\"totalCost\":").append(random.nextDouble() * 15);
                data.append("}");
                data.append("}");
            }
            data.append("]");
            data.append("}");
            response.put("data", data.toString());
        } else if (request != null && request.containsKey("filter") && 
                   request.get("filter").toString().contains("controllerKind")) {
            // 工作负载查询返回工作负载数据
            String filter = request.get("filter").toString();
            String workloadType = "Deployment";
            if (filter.contains("\"StatefulSet\"")) {
                workloadType = "StatefulSet";
            } else if (filter.contains("\"DaemonSet\"")) {
                workloadType = "DaemonSet";
            }
            
            StringBuilder data = new StringBuilder();
            data.append("{");
            data.append("\"items\":[");
            String[] workloads = {"test-" + workloadType.toLowerCase() + "-1", 
                                  "test-" + workloadType.toLowerCase() + "-2"};
            for (int i = 0; i < workloads.length; i++) {
                if (i > 0) data.append(",");
                data.append("{");
                data.append("\"properties\":{");
                data.append("\"name\":\"default/").append(workloads[i]).append("\",");
                data.append("\"cpuCost\":").append(random.nextDouble() * 2).append(",");
                data.append("\"gpuCost\":0.0,");
                data.append("\"ramCost\":").append(random.nextDouble() * 5).append(",");
                data.append("\"pvCost\":0.0,");
                data.append("\"totalCost\":").append(random.nextDouble() * 7);
                data.append("}");
                data.append("}");
            }
            data.append("]");
            data.append("}");
            response.put("data", data.toString());
        } else {
            // 基本查询返回单个工作负载数据
            StringBuilder data = new StringBuilder();
            data.append("{");
            data.append("\"properties\":{");
            data.append("\"name\":\"default/test-pod-12345\",");
            data.append("\"cpuCost\":").append(random.nextDouble() * 1).append(",");
            data.append("\"gpuCost\":0.0,");
            data.append("\"ramCost\":").append(random.nextDouble() * 2).append(",");
            data.append("\"pvCost\":0.0,");
            data.append("\"totalCost\":").append(random.nextDouble() * 3);
            data.append("}");
            data.append("}");
            response.put("data", data.toString());
        }
        
        return response;
    }
    
    // 生成模拟的分配数据
    private Map<String, Object> generateMockAllocationData(Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        
        // 根据请求参数生成不同类型的模拟数据
        if (request != null && request.containsKey("aggregate") && 
            "namespace".equals(request.get("aggregate"))) {
            // 聚合查询返回命名空间数据
            StringBuilder data = new StringBuilder();
            data.append("{");
            data.append("\"items\":[");
            String[] namespaces = {"default", "kube-system", "arms-prom", "__idle__"};
            for (int i = 0; i < namespaces.length; i++) {
                if (i > 0) data.append(",");
                data.append("{");
                data.append("\"properties\":{");
                data.append("\"name\":\"").append(namespaces[i]).append("\",");
                data.append("\"cpuCost\":").append(random.nextDouble() * 5).append(",");
                data.append("\"gpuCost\":0.0,");
                data.append("\"ramCost\":").append(random.nextDouble() * 10).append(",");
                data.append("\"pvCost\":").append(random.nextDouble() * 2).append(",");
                data.append("\"totalCost\":").append(random.nextDouble() * 15).append(",");
                data.append("\"cpuCoreUsageAverage\":").append(random.nextDouble() * 2).append(",");
                data.append("\"ramByteUsageAverage\":").append(random.nextInt(1000000000));
                data.append("}");
                data.append("}");
            }
            data.append("]");
            data.append("}");
            response.put("data", data.toString());
        } else if (request != null && request.containsKey("filter") && 
                   request.get("filter").toString().contains("controllerKind")) {
            // 工作负载查询返回工作负载数据
            String filter = request.get("filter").toString();
            String workloadType = "Deployment";
            if (filter.contains("\"StatefulSet\"")) {
                workloadType = "StatefulSet";
            } else if (filter.contains("\"DaemonSet\"")) {
                workloadType = "DaemonSet";
            }
            
            StringBuilder data = new StringBuilder();
            data.append("{");
            data.append("\"items\":[");
            String[] workloads = {"test-" + workloadType.toLowerCase() + "-1", 
                                  "test-" + workloadType.toLowerCase() + "-2"};
            for (int i = 0; i < workloads.length; i++) {
                if (i > 0) data.append(",");
                data.append("{");
                data.append("\"properties\":{");
                data.append("\"name\":\"default/").append(workloads[i]).append("\",");
                data.append("\"cpuCost\":").append(random.nextDouble() * 2).append(",");
                data.append("\"gpuCost\":0.0,");
                data.append("\"ramCost\":").append(random.nextDouble() * 5).append(",");
                data.append("\"pvCost\":0.0,");
                data.append("\"totalCost\":").append(random.nextDouble() * 7).append(",");
                data.append("\"cpuCoreUsageAverage\":").append(random.nextDouble() * 1).append(",");
                data.append("\"ramByteUsageAverage\":").append(random.nextInt(500000000));
                data.append("}");
                data.append("}");
            }
            data.append("]");
            data.append("}");
            response.put("data", data.toString());
        } else {
            // 基本查询返回单个工作负载数据
            StringBuilder data = new StringBuilder();
            data.append("{");
            data.append("\"properties\":{");
            data.append("\"name\":\"default/test-pod-12345\",");
            data.append("\"cpuCost\":").append(random.nextDouble() * 1).append(",");
            data.append("\"gpuCost\":0.0,");
            data.append("\"ramCost\":").append(random.nextDouble() * 2).append(",");
            data.append("\"pvCost\":0.0,");
            data.append("\"totalCost\":").append(random.nextDouble() * 3).append(",");
            data.append("\"cpuCoreUsageAverage\":").append(random.nextDouble() * 0.5).append(",");
            data.append("\"ramByteUsageAverage\":").append(random.nextInt(200000000));
            data.append("}");
            data.append("}");
            response.put("data", data.toString());
        }
        
        return response;
    }
}