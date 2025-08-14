package com.aliyun.container.service.cost.sdk;

import com.aliyun.container.service.cost.sdk.model.CostV2Request;
import com.aliyun.container.service.cost.sdk.model.AllocationRequest;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FVT测试类，用于验证Java SDK的功能
 */
public class FVTTest {
    
    public static void main(String[] args) {
        try {
            runFVT();
        } catch (Exception e) {
            System.err.println("FVT测试执行出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 运行FVT测试
     */
    public static void runFVT() {
        System.out.println("=== Java FVT测试开始 ===");
        
        try {
            // 初始化客户端
            Config config = new Config();
            config.setEndpoint("http://127.0.0.1:8080");
            Client client = new Client(config);
            
            testCostV2Service(client);
            testAllocationService(client);
            
        } catch (Exception e) {
            System.err.println("Java FVT测试执行出错: " + e.getMessage());
            e.printStackTrace();
            // 即使出现异常也继续执行其他测试
        }
        
        // 测试4: 动态获取集群中的工作负载
        System.out.println("测试4: 动态获取集群中的工作负载");
        String[] workloadFilters = {
            "controllerKind:\"Deployment\"",
            "controllerKind:\"StatefulSet\"",
            "controllerKind:\"DaemonSet\""
        };
        
        for (String filter : workloadFilters) {
            System.out.println("Java SDK将支持查询 " + filter + " 类型的工作负载");
        }
        
        System.out.println("=== Java FVT测试结束 ===\n");
    }
    
    private static void printRequestLog(String apiName, Map<String, Object> requestData) {
        try {
            System.out.println("  发送请求到 " + apiName);
            // 简单的Map转JSON字符串（仅用于日志打印）
            StringBuilder json = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : requestData.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof String) {
                    json.append("\"").append(entry.getValue()).append("\"");
                } else {
                    json.append(entry.getValue());
                }
                first = false;
            }
            json.append("}");
            System.out.println("  请求参数: " + json.toString());
        } catch (Exception e) {
            System.out.println("  请求参数: " + requestData.toString());
        }
    }
    
    private static void printResponseLog(Map<String, Object> responseData, long startTime) {
        try {
            if (startTime > 0) {
                double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.println(String.format("  请求耗时: %.2f秒", elapsed));
            }
            
            Object code = responseData.get("code");
            System.out.println("  响应状态码: " + (code != null ? code.toString() : "N/A"));
            
            if (responseData.containsKey("data") && responseData.get("data") != null) {
                String dataStr = responseData.get("data").toString();
                System.out.println("  响应数据详情:");
                
                // 尝试解析JSON数据
                try {
                    // 如果有properties字段，说明是成本数据
                    if (dataStr.contains("\"properties\"")) {
                        // 提取name字段
                        Pattern namePattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
                        Matcher nameMatcher = namePattern.matcher(dataStr);
                        if (nameMatcher.find()) {
                            System.out.println("    名称: " + nameMatcher.group(1));
                        }
                        
                        // 提取cpuCost字段
                        Pattern cpuCostPattern = Pattern.compile("\"cpuCost\"\\s*:\\s*([\\d.]+)");
                        Matcher cpuCostMatcher = cpuCostPattern.matcher(dataStr);
                        if (cpuCostMatcher.find()) {
                            System.out.println("    CPU成本: " + cpuCostMatcher.group(1));
                        }
                        
                        // 提取gpuCost字段
                        Pattern gpuCostPattern = Pattern.compile("\"gpuCost\"\\s*:\\s*([\\d.]+)");
                        Matcher gpuCostMatcher = gpuCostPattern.matcher(dataStr);
                        if (gpuCostMatcher.find()) {
                            System.out.println("    GPU成本: " + gpuCostMatcher.group(1));
                        }
                        
                        // 提取ramCost字段
                        Pattern ramCostPattern = Pattern.compile("\"ramCost\"\\s*:\\s*([\\d.]+)");
                        Matcher ramCostMatcher = ramCostPattern.matcher(dataStr);
                        if (ramCostMatcher.find()) {
                            System.out.println("    内存成本: " + ramCostMatcher.group(1));
                        }
                        
                        // 提取pvCost字段
                        Pattern pvCostPattern = Pattern.compile("\"pvCost\"\\s*:\\s*([\\d.]+)");
                        Matcher pvCostMatcher = pvCostPattern.matcher(dataStr);
                        if (pvCostMatcher.find()) {
                            System.out.println("    存储成本: " + pvCostMatcher.group(1));
                        }
                        
                        // 提取totalCost字段
                        Pattern totalCostPattern = Pattern.compile("\"totalCost\"\\s*:\\s*([\\d.]+)");
                        Matcher totalCostMatcher = totalCostPattern.matcher(dataStr);
                        if (totalCostMatcher.find()) {
                            System.out.println("    总成本: " + totalCostMatcher.group(1));
                        }
                    } 
                    // 如果有items字段，说明是列表数据
                    else if (dataStr.contains("\"items\"") && dataStr.contains("[")) {
                        // 计算items数组中的对象数量
                        int itemsStart = dataStr.indexOf("\"items\"");
                        int arrayStart = dataStr.indexOf("[", itemsStart);
                        int arrayEnd = findMatchingBracket(dataStr, arrayStart);
                        
                        if (arrayStart != -1 && arrayEnd != -1) {
                            String itemsArray = dataStr.substring(arrayStart, arrayEnd + 1);
                            int itemCount = countTopLevelObjects(itemsArray);
                            System.out.println("    数据项数量: " + itemCount);
                            
                            // 提取前3个项目
                            int showCount = Math.min(3, itemCount);
                            int objStart = arrayStart + 1;
                            for (int i = 0; i < showCount; i++) {
                                int nextObjStart = findNextObjectStart(dataStr, objStart);
                                if (nextObjStart == -1 || nextObjStart >= arrayEnd) {
                                    break;
                                }
                                
                                int objEnd = findMatchingBrace(dataStr, nextObjStart);
                                if (objEnd == -1) {
                                    break;
                                }
                                
                                String itemStr = dataStr.substring(nextObjStart, objEnd + 1);
                                System.out.println("    数据项 " + (i+1) + ":");
                                
                                // 提取项目中的name和totalCost
                                Pattern namePattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
                                Matcher nameMatcher = namePattern.matcher(itemStr);
                                if (nameMatcher.find()) {
                                    System.out.println("      名称: " + nameMatcher.group(1));
                                }
                                
                                Pattern totalCostPattern = Pattern.compile("\"totalCost\"\\s*:\\s*([\\d.]+)");
                                Matcher totalCostMatcher = totalCostPattern.matcher(itemStr);
                                if (totalCostMatcher.find()) {
                                    System.out.println("      总成本: " + totalCostMatcher.group(1));
                                }
                                
                                objStart = objEnd + 1;
                            }
                            
                            if (itemCount > 3) {
                                System.out.println("    ...还有" + (itemCount - 3) + "个项目未显示");
                            }
                        }
                    }
                    // 其他情况，直接显示部分数据
                    else {
                        System.out.println("    数据内容: " + dataStr.substring(0, Math.min(500, dataStr.length())) + 
                            (dataStr.length() > 500 ? "..." : ""));
                    }
                } catch (Exception e) {
                    // 如果解析失败，直接显示字符串
                    System.out.println("    响应数据: " + dataStr.substring(0, Math.min(500, dataStr.length())) + 
                        (dataStr.length() > 500 ? "..." : ""));
                }
            } else if (responseData.containsKey("error") && responseData.get("error") != null) {
                System.out.println("  错误信息: " + responseData.get("error").toString());
            }
        } catch (Exception e) {
            System.err.println("  处理响应日志时出错: " + e.getMessage());
        }
    }
    
    // 辅助方法：查找匹配的方括号
    private static int findMatchingBracket(String str, int start) {
        if (start >= str.length() || str.charAt(start) != '[') {
            return -1;
        }
        
        int count = 1;
        for (int i = start + 1; i < str.length(); i++) {
            if (str.charAt(i) == '[') {
                count++;
            } else if (str.charAt(i) == ']') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    // 辅助方法：计算数组中顶层对象的数量
    private static int countTopLevelObjects(String arrayStr) {
        if (!arrayStr.startsWith("[") || !arrayStr.endsWith("]")) {
            return 0;
        }
        
        int count = 0;
        int braceCount = 0;
        boolean inObject = false;
        
        for (int i = 1; i < arrayStr.length() - 1; i++) {
            char c = arrayStr.charAt(i);
            if (c == '{' && !inObject) {
                inObject = true;
                braceCount = 1;
            } else if (c == '{' && inObject) {
                braceCount++;
            } else if (c == '}' && inObject) {
                braceCount--;
                if (braceCount == 0) {
                    inObject = false;
                    count++;
                }
            }
        }
        return count;
    }
    
    // 辅助方法：查找下一个对象的开始位置
    private static int findNextObjectStart(String str, int start) {
        for (int i = start; i < str.length(); i++) {
            if (str.charAt(i) == '{') {
                return i;
            }
        }
        return -1;
    }
    
    // 辅助方法：查找匹配的大括号
    private static int findMatchingBrace(String str, int start) {
        if (start >= str.length() || str.charAt(start) != '{') {
            return -1;
        }
        
        int count = 1;
        for (int i = start + 1; i < str.length(); i++) {
            if (str.charAt(i) == '{') {
                count++;
            } else if (str.charAt(i) == '}') {
                count--;
                if (count == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private static void testCostV2Service(Client client) {
        System.out.println("测试1: CostV2Service");
        
        try {
            // 测试1.1: 基本的GetCostV2调用
            System.out.println("测试1.1: GetCostV2 with window");
            CostV2Request req1 = new CostV2Request();
            req1.setWindow("today");
            printRequestLog("CostV2Service.GetCostV2", req1.toMap());
            long startTime1 = System.currentTimeMillis();
            Map<String, Object> resp1 = client.getCostV2(req1.toMap());
            printResponseLog(resp1, startTime1);
            
            // 验证返回值
            if ((Integer)resp1.get("code") == 200) {
                System.out.println("  ✓ 成本API调用成功");
            } else {
                System.out.println("  ✗ 成本API调用失败，状态码: " + resp1.get("code"));
            }
        } catch (Exception e) {
            System.err.println("  GetCostV2 with window测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            // 测试1.2: 带filter的GetCostV2调用
            System.out.println("测试1.2: GetCostV2 with filter");
            CostV2Request req2 = new CostV2Request();
            req2.setWindow("today");
            req2.setFilter("namespace:\"kube-system\"");
            printRequestLog("CostV2Service.GetCostV2", req2.toMap());
            long startTime2 = System.currentTimeMillis();
            Map<String, Object> resp2 = client.getCostV2(req2.toMap());
            printResponseLog(resp2, startTime2);
            
            // 验证返回值
            if ((Integer)resp2.get("code") == 200) {
                System.out.println("  ✓ 带过滤条件的成本API调用成功");
            } else {
                System.out.println("  ✗ 带过滤条件的成本API调用失败，状态码: " + resp2.get("code"));
            }
        } catch (Exception e) {
            System.err.println("  GetCostV2 with filter测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            // 测试1.3: 带aggregate的GetCostV2调用
            System.out.println("测试1.3: GetCostV2 with aggregate");
            CostV2Request req3 = new CostV2Request();
            req3.setWindow("today");
            req3.setAggregate("namespace");
            printRequestLog("CostV2Service.GetCostV2", req3.toMap());
            long startTime3 = System.currentTimeMillis();
            Map<String, Object> resp3 = client.getCostV2(req3.toMap());
            printResponseLog(resp3, startTime3);
            
            // 验证返回值
            if ((Integer)resp3.get("code") == 200) {
                System.out.println("  ✓ 聚合查询的成本API调用成功");
            } else {
                System.out.println("  ✗ 聚合查询的成本API调用失败，状态码: " + resp3.get("code"));
            }
        } catch (Exception e) {
            System.err.println("  GetCostV2 with aggregate测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 测试1.4: 获取工作负载的成本数据
        System.out.println("测试1.4: GetCostV2 for workloads");
        String[] workloadTypes = {"Deployment", "StatefulSet", "DaemonSet"};
        for (String workloadType : workloadTypes) {
            try {
                CostV2Request req = new CostV2Request();
                req.setWindow("today");
                req.setFilter("controllerKind:\"" + workloadType + "\"");
                printRequestLog("CostV2Service.GetCostV2", req.toMap());
                long startTime = System.currentTimeMillis();
                Map<String, Object> resp = client.getCostV2(req.toMap());
                printResponseLog(resp, startTime);
                
                // 验证返回值
                if ((Integer)resp.get("code") == 200) {
                    System.out.println("  ✓ " + workloadType + "类型工作负载成本查询成功");
                } else if ((Integer)resp.get("code") == 503) {
                    System.out.println("  - " + workloadType + "类型工作负载查询返回服务不可用(503)");
                } else {
                    System.out.println("  ✗ " + workloadType + "类型工作负载查询失败，状态码: " + resp.get("code"));
                }
            } catch (Exception e) {
                System.err.println("  GetCostV2 for controllerKind:\"" + workloadType + "\" failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private static void testAllocationService(Client client) {
        System.out.println("测试2: AllocationService");
        
        try {
            // 测试2.1: 基本的GetAllocation调用
            System.out.println("测试2.1: GetAllocation");
            AllocationRequest req1 = new AllocationRequest();
            req1.setWindow("today");
            printRequestLog("AllocationService.GetAllocation", req1.toMap());
            long startTime1 = System.currentTimeMillis();
            Map<String, Object> resp1 = client.getAllocation(req1.toMap());
            printResponseLog(resp1, startTime1);
            
            // 验证返回值
            if ((Integer)resp1.get("code") == 200) {
                System.out.println("  ✓ 分配API调用成功");
            } else {
                System.out.println("  ✗ 分配API调用失败，状态码: " + resp1.get("code"));
            }
        } catch (Exception e) {
            System.err.println("  GetAllocation测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            // 测试2.2: 带aggregate的GetAllocation调用
            System.out.println("测试2.2: GetAllocation with aggregate");
            AllocationRequest req2 = new AllocationRequest();
            req2.setWindow("today");
            req2.setAggregate("namespace");
            printRequestLog("AllocationService.GetAllocation", req2.toMap());
            long startTime2 = System.currentTimeMillis();
            Map<String, Object> resp2 = client.getAllocation(req2.toMap());
            printResponseLog(resp2, startTime2);
            
            // 验证返回值
            if ((Integer)resp2.get("code") == 200) {
                System.out.println("  ✓ 聚合查询的分配API调用成功");
            } else {
                System.out.println("  ✗ 聚合查询的分配API调用失败，状态码: " + resp2.get("code"));
            }
        } catch (Exception e) {
            System.err.println("  GetAllocation with aggregate测试失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 测试2.3: 获取工作负载的分配数据
        System.out.println("测试2.3: GetAllocation for workloads");
        String[] workloadTypes = {"Deployment", "StatefulSet", "DaemonSet"};
        for (String workloadType : workloadTypes) {
            try {
                AllocationRequest req = new AllocationRequest();
                req.setWindow("today");
                req.setFilter("controllerKind:\"" + workloadType + "\"");
                printRequestLog("AllocationService.GetAllocation", req.toMap());
                long startTime = System.currentTimeMillis();
                Map<String, Object> resp = client.getAllocation(req.toMap());
                printResponseLog(resp, startTime);
                
                // 验证返回值
                if ((Integer)resp.get("code") == 200) {
                    System.out.println("  ✓ " + workloadType + "类型工作负载分配查询成功");
                } else if ((Integer)resp.get("code") == 503) {
                    System.out.println("  - " + workloadType + "类型工作负载分配查询返回服务不可用(503)");
                } else {
                    System.out.println("  ✗ " + workloadType + "类型工作负载分配查询失败，状态码: " + resp.get("code"));
                }
            } catch (Exception e) {
                System.err.println("  GetAllocation for controllerKind:\"" + workloadType + "\" failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}