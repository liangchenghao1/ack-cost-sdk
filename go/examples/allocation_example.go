package main

import (
	"context"
	"fmt"
	"log"

	cost "github.com/AliyunContainerService/cost-sdk/go"
)

func main() {
	// 创建客户端
	client, err := cost.NewClient(&cost.Config{
		APIServer:  "https://kubernetes.default.svc",
		RetryCount: 3,
		RetryWait:  2,
	})
	if err != nil {
		log.Fatal(err)
	}

	// 示例: 获取某个DaemonSet昨天分摊集群账单的费用
	fmt.Println("=== 获取DaemonSet业务分摊账单 ===")
	example(client)
}

func example(client *cost.Client) {
	// 获取某个DaemonSet昨天分摊集群账单的费用
	response, err := client.Allocation.GetAllocation(context.Background(), &cost.AllocationRequest{
		Window: "yesterday",
		Filter: `namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"`,
	})
	if err != nil {
		log.Printf("Error: %v", err)
		return
	}

	for _, data := range response.Data {
		for name, allocation := range data {
			fmt.Printf("Name: %s, Allocation Cost: %.3f, CPU Request: %.2f, Memory Request: %.0f\n",
				name, allocation.Cost, allocation.CPUCoreRequestAverage, allocation.RAMByteRequestAverage)
		}
	}
}