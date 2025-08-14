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

	// 示例1: 查询DaemonSet昨天的估算成本明细
	fmt.Println("=== 示例1: 查询DaemonSet昨天的估算成本明细 ===")
	example1(client)

	// 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
	fmt.Println("\n=== 示例2: 获取某个Pod三小时内的估算成本明细 ===")
	example2(client)

	// 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
	fmt.Println("\n=== 示例3: 按Label聚合应用成本 ===")
	example3(client)
}

func example1(client *cost.Client) {
	// 查询DaemonSet昨天的成本
	response, err := client.CostV2.GetCostV2(context.Background(), &cost.CostV2Request{
		Window: "yesterday",
		Filter: `namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"`,
	})
	if err != nil {
		log.Printf("Error: %v", err)
		return
	}

	for _, data := range response.Data {
		for name, cost := range data {
			fmt.Printf("Name: %s, Cost: %.3f, CPU Request: %.2f, Memory Request: %.0f\n",
				name, cost.Cost, cost.CPUCoreRequestAverage, cost.RAMByteRequestAverage)
		}
	}
}

func example2(client *cost.Client) {
	// 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
	response, err := client.CostV2.GetCostV2(context.Background(), &cost.CostV2Request{
		Window: "2024-03-24T00:00:00Z,2024-03-24T03:00:00Z",
		Step:   "1h",
		Filter: `namespace:"kube-system"+pod:"terway-eniip-kz68n"`,
	})
	if err != nil {
		log.Printf("Error: %v", err)
		return
	}

	for _, data := range response.Data {
		for name, cost := range data {
			fmt.Printf("Name: %s, Period: %s - %s, Cost: %.3f\n",
				name, cost.Start, cost.End, cost.Cost)
		}
	}
}

func example3(client *cost.Client) {
	// 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
	idle := true
	response, err := client.CostV2.GetCostV2(context.Background(), &cost.CostV2Request{
		Window:    "2h",
		Aggregate: "label:app",
		Idle:      &idle,
	})
	if err != nil {
		log.Printf("Error: %v", err)
		return
	}

	for _, data := range response.Data {
		for name, cost := range data {
			fmt.Printf("App: %s, Cost: %.3f, CPU Usage: %.3f, Memory Usage: %.0f\n",
				name, cost.Cost, cost.CPUCoreUsageAverage, cost.RAMByteUsageAverage)
		}
	}
}