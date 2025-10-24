package main

import (
	"context"
	"fmt"
	"log"
	"os"

	openapi "github.com/AliyunContainerService/cost-sdk"
)

func main() {
	// Check if kubeconfig file is provided
	kubeconfigPath := ""
	if len(os.Args) > 1 {
		kubeconfigPath = os.Args[1]
	}

	// Create client with kubeconfig authentication
	proxyPath := "/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy"
	client, err := openapi.NewAPIClientWithKubeConfigAndProxy(kubeconfigPath, proxyPath)
	if err != nil {
		log.Fatalf("Failed to create client: %v", err)
	}

	fmt.Println("Client created successfully!")
	fmt.Printf("Server URL: %s\n", client.GetConfig().Servers[0].URL)

	ctx := context.Background()

	// Query cost data for the past hour
	fmt.Println("\n=== Querying cost data for the past hour ===")
	result, httpResp, err := client.DefaultAPI.GetCost(ctx).
		Window("1h").
		Filter(`namespace:"kube-system"+controllerKind:"ReplicaSet"+label[app]:"ack-cost-exporter"`).
		Execute()

	if err != nil {
		log.Printf("API call failed: %v", err)
	} else {
		fmt.Printf("API call successful! Status code: %d\n", httpResp.StatusCode)
		fmt.Printf("Data overview:\n")
		if result != nil && result.Data != nil {
			fmt.Printf("   - Number of data entries: %d\n", len(result.Data))

			// Display each time range's data
			for i, timeRangeData := range result.Data {
				fmt.Printf("\n   Time Range %d:\n", i+1)

				// Display each pod's cost data in this time range
				for podName, allocation := range timeRangeData {
					fmt.Printf("\n     Pod: %s\n", podName)

					// Display basic allocation info
					if allocation.HasName() {
						fmt.Printf("       Name: %s\n", allocation.GetName())
					}
					if allocation.HasStart() {
						fmt.Printf("       Start: %s\n", allocation.GetStart().Format("2006-01-02 15:04:05"))
					}
					if allocation.HasEnd() {
						fmt.Printf("       End: %s\n", allocation.GetEnd().Format("2006-01-02 15:04:05"))
					}

					// Display resource usage
					if allocation.HasCpuCoreRequestAverage() {
						fmt.Printf("       CPU Request Average: %.2f cores\n", allocation.GetCpuCoreRequestAverage())
					}
					if allocation.HasCpuCoreUsageAverage() {
						fmt.Printf("       CPU Usage Average: %.2f cores\n", allocation.GetCpuCoreUsageAverage())
					}
					if allocation.HasRamByteRequestAverage() {
						fmt.Printf("       RAM Request Average: %.2f MB\n", allocation.GetRamByteRequestAverage()/1024/1024)
					}
					if allocation.HasRamByteUsageAverage() {
						fmt.Printf("       RAM Usage Average: %.2f MB\n", allocation.GetRamByteUsageAverage()/1024/1024)
					}

					// Display cost information
					if allocation.HasCost() {
						fmt.Printf("       Cost: %.4f\n", allocation.GetCost())
					}
					if allocation.HasCostRatio() {
						fmt.Printf("       Cost Ratio: %.2f%%\n", allocation.GetCostRatio()*100)
					}
					if allocation.HasCustomCost() {
						fmt.Printf("       Custom Cost: %.4f\n", allocation.GetCustomCost())
					}

					// Display properties if available
					if allocation.HasProperties() {
						props := allocation.GetProperties()
						fmt.Printf("       Properties:\n")
						if props.HasNamespace() {
							fmt.Printf("         Namespace: %s\n", props.GetNamespace())
						}
						if props.HasNode() {
							fmt.Printf("         Node: %s\n", props.GetNode())
						}
						if props.HasController() {
							fmt.Printf("         Controller: %s\n", props.GetController())
						}
						if props.HasControllerKind() {
							fmt.Printf("         Controller Kind: %s\n", props.GetControllerKind())
						}
						if props.HasCluster() {
							fmt.Printf("         Cluster: %s\n", props.GetCluster())
						}
					}
				}
			}
		}
	}

	fmt.Println("\nCost query example completed!")
}
