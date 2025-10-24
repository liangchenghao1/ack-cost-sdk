package examples;

import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.AllocationSetRange;

import java.io.IOException;

/**
 * Cost Query Example
 */
public class CostQueryExample {
    
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("Java SDK Kubeconfig Cost Query Example");
        System.out.println("============================================================");
        
        try {
            // Create API client with kubeconfig and proxy
            String proxyPath = "/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy";
            ApiClient apiClient = Configuration.newApiClientWithKubeconfigAndProxy(null, proxyPath);
            
            System.out.println("Client created successfully!");
            System.out.println("Server URL: " + apiClient.getBasePath());
            
            // Create API instance
            DefaultApi defaultApi = new DefaultApi(apiClient);
            System.out.println("API instance created successfully");
            
            // Query cost data
            System.out.println("Querying cost data for ack-cost-exporter in kube-system namespace...");
            
            // Make the API call
            AllocationSetRange response = defaultApi.getCost(
                "1h",  // window
                "namespace:\"kube-system\"+controllerKind:\"ReplicaSet\"+label[app]:\"ack-cost-exporter\"",  // filter
                null,  // step
                null,  // aggregate
                null,  // idle
                null,  // shareIdle
                null,  // shareSplit
                null,  // idleByNode
                null   // format
            );
            
            System.out.println("Successfully retrieved cost data");
            System.out.println("Response data structure:");
            
            if (response != null && response.getData() != null) {
                System.out.println("   - Number of data entries: " + response.getData().size());
                
                // Display each time range's data
                for (int i = 0; i < response.getData().size(); i++) {
                    Object dataItem = response.getData().get(i);
                    System.out.println("\n   Time Range " + (i + 1) + ":");
                    
                    if (dataItem instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> itemMap = (java.util.Map<String, Object>) dataItem;
                        
                        // Display each pod's cost data in this time range
                        for (java.util.Map.Entry<String, Object> entry : itemMap.entrySet()) {
                            String resourceName = entry.getKey();
                            Object allocationObj = entry.getValue();
                            
                            System.out.println("\n     Pod: " + resourceName);
                            
                            if (allocationObj instanceof org.openapitools.client.model.Allocation) {
                                org.openapitools.client.model.Allocation allocation = (org.openapitools.client.model.Allocation) allocationObj;
                                
                                // Display basic allocation info
                                if (allocation.getName() != null) {
                                    System.out.println("       Name: " + allocation.getName());
                                }
                                if (allocation.getStart() != null) {
                                    System.out.println("       Start: " + allocation.getStart());
                                }
                                if (allocation.getEnd() != null) {
                                    System.out.println("       End: " + allocation.getEnd());
                                }
                                
                                // Display resource usage
                                if (allocation.getCpuCoreRequestAverage() != null) {
                                    System.out.println("       CPU Request Average: " + String.format("%.2f", allocation.getCpuCoreRequestAverage()) + " cores");
                                }
                                if (allocation.getCpuCoreUsageAverage() != null) {
                                    System.out.println("       CPU Usage Average: " + String.format("%.2f", allocation.getCpuCoreUsageAverage()) + " cores");
                                }
                                if (allocation.getRamByteRequestAverage() != null) {
                                    System.out.println("       RAM Request Average: " + String.format("%.2f", allocation.getRamByteRequestAverage() / (1024 * 1024)) + " MB");
                                }
                                if (allocation.getRamByteUsageAverage() != null) {
                                    System.out.println("       RAM Usage Average: " + String.format("%.2f", allocation.getRamByteUsageAverage() / (1024 * 1024)) + " MB");
                                }
                                
                                // Display cost information
                                if (allocation.getCost() != null) {
                                    System.out.println("       Cost: " + String.format("%.4f", allocation.getCost()));
                                }
                                if (allocation.getCostRatio() != null) {
                                    System.out.println("       Cost Ratio: " + String.format("%.2f", allocation.getCostRatio() * 100) + "%");
                                }
                                if (allocation.getCustomCost() != null) {
                                    System.out.println("       Custom Cost: " + String.format("%.4f", allocation.getCustomCost()));
                                }
                                
                                // Display properties if available
                                org.openapitools.client.model.AllocationProperties properties = allocation.getProperties();
                                if (properties != null) {
                                    System.out.println("       Properties:");
                                    if (properties.getNamespace() != null) {
                                        System.out.println("         Namespace: " + properties.getNamespace());
                                    }
                                    if (properties.getNode() != null) {
                                        System.out.println("         Node: " + properties.getNode());
                                    }
                                    if (properties.getController() != null) {
                                        System.out.println("         Controller: " + properties.getController());
                                    }
                                    if (properties.getControllerKind() != null) {
                                        System.out.println("         Controller Kind: " + properties.getControllerKind());
                                    }
                                    if (properties.getCluster() != null) {
                                        System.out.println("         Cluster: " + properties.getCluster());
                                    }
                                }
                            } else if (allocationObj instanceof java.util.Map) {
                                @SuppressWarnings("unchecked")
                                java.util.Map<String, Object> allocation = (java.util.Map<String, Object>) allocationObj;
                                
                                // Display basic allocation info
                                if (allocation.get("name") != null) {
                                    System.out.println("       Name: " + allocation.get("name"));
                                }
                                if (allocation.get("start") != null) {
                                    System.out.println("       Start: " + allocation.get("start"));
                                }
                                if (allocation.get("end") != null) {
                                    System.out.println("       End: " + allocation.get("end"));
                                }
                                
                                // Display resource usage
                                Object cpuRequestObj = allocation.get("cpuCoreRequestAverage");
                                if (cpuRequestObj != null) {
                                    System.out.println("       CPU Request Average: " + String.format("%.2f", ((Number) cpuRequestObj).doubleValue()) + " cores");
                                }
                                Object cpuUsageObj = allocation.get("cpuCoreUsageAverage");
                                if (cpuUsageObj != null) {
                                    System.out.println("       CPU Usage Average: " + String.format("%.2f", ((Number) cpuUsageObj).doubleValue()) + " cores");
                                }
                                Object ramRequestObj = allocation.get("ramByteRequestAverage");
                                if (ramRequestObj != null) {
                                    System.out.println("       RAM Request Average: " + String.format("%.2f", ((Number) ramRequestObj).doubleValue() / (1024 * 1024)) + " MB");
                                }
                                Object ramUsageObj = allocation.get("ramByteUsageAverage");
                                if (ramUsageObj != null) {
                                    System.out.println("       RAM Usage Average: " + String.format("%.2f", ((Number) ramUsageObj).doubleValue() / (1024 * 1024)) + " MB");
                                }
                                
                                // Display cost information
                                Object costObj = allocation.get("cost");
                                if (costObj != null) {
                                    System.out.println("       Cost: " + String.format("%.4f", ((Number) costObj).doubleValue()));
                                }
                                Object costRatioObj = allocation.get("costRatio");
                                if (costRatioObj != null) {
                                    System.out.println("       Cost Ratio: " + String.format("%.2f", ((Number) costRatioObj).doubleValue() * 100) + "%");
                                }
                                Object customCostObj = allocation.get("customCost");
                                if (customCostObj != null) {
                                    System.out.println("       Custom Cost: " + String.format("%.4f", ((Number) customCostObj).doubleValue()));
                                }
                                
                                // Display properties if available
                                @SuppressWarnings("unchecked")
                                java.util.Map<String, Object> properties = (java.util.Map<String, Object>) allocation.get("properties");
                                if (properties != null) {
                                    System.out.println("       Properties:");
                                    if (properties.get("namespace") != null) {
                                        System.out.println("         Namespace: " + properties.get("namespace"));
                                    }
                                    if (properties.get("node") != null) {
                                        System.out.println("         Node: " + properties.get("node"));
                                    }
                                    if (properties.get("controller") != null) {
                                        System.out.println("         Controller: " + properties.get("controller"));
                                    }
                                    if (properties.get("controllerKind") != null) {
                                        System.out.println("         Controller Kind: " + properties.get("controllerKind"));
                                    }
                                    if (properties.get("cluster") != null) {
                                        System.out.println("         Cluster: " + properties.get("cluster"));
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("   - No matching resources found");
            }
            
        } catch (IOException e) {
            System.err.println("Kubeconfig loading failed: " + e.getMessage());
            System.err.println("Please ensure:");
            System.err.println("  1. KUBECONFIG environment variable is set, or");
            System.err.println("  2. kubeconfig file exists at ~/.kube/config");
            System.exit(1);
        } catch (ApiException e) {
            System.err.println("API call failed: " + e.getMessage());
            System.err.println("Status code: " + e.getCode());
            System.err.println("Response body: " + e.getResponseBody());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Other error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        
        System.out.println("============================================================");
        System.out.println("Example completed successfully!");
    }
}