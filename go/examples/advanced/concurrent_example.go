package main

import (
	"context"
	"fmt"
	"sync"
	"time"

	costsdk "github.com/AliyunContainerService/cost-sdk/go"
)

// 并发调用示例
func main() {
	// 创建客户端
	client, err := costsdk.NewClient(&costsdk.Config{
		APIServer: "https://kubernetes.default.svc",
	})
	if err != nil {
		panic(err)
	}

	// 使用WaitGroup等待所有goroutine完成
	var wg sync.WaitGroup
	
	// 并发执行多个请求
	wg.Add(3)
	
	// 启动第一个goroutine查询DaemonSet成本
	go func() {
		defer wg.Done()
		queryDaemonSetCost(client)
	}()
	
	// 启动第二个goroutine查询Pod成本
	go func() {
		defer wg.Done()
		queryPodCost(client)
	}()
	
	// 启动第三个goroutine查询按标签聚合的成本
	go func() {
		defer wg.Done()
		queryAggregatedCost(client)
	}()
	
	// 等待所有goroutine完成
	wg.Wait()
	
	fmt.Println("所有并发请求已完成")
}

// 查询DaemonSet昨天的成本
func queryDaemonSetCost(client *costsdk.Client) {
	fmt.Println("=== 查询DaemonSet昨天的成本 ===")
	
	request := &costsdk.CostV2Request{
		Window: "yesterday",
		Filter: `namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"`,
	}
	
	ctx := context.Background()
	start := time.Now()
	response, err := client.CostV2.GetCostV2(ctx, request)
	if err != nil {
		fmt.Printf("查询DaemonSet成本失败: %v\n", err)
		return
	}
	
	fmt.Printf("查询DaemonSet成本耗时: %v\n", time.Since(start))
	
	for _, data := range response.Data {
		for name, cost := range data {
			fmt.Printf("Name: %s, Cost: %.3f, CPU Request: %.2f, Memory Request: %.0f\n",
				name, cost.Cost, cost.CPUCoreRequestAverage, cost.RAMByteRequestAverage)
		}
	}
}

// 查询特定Pod三小时内的成本明细
func queryPodCost(client *costsdk.Client) {
	fmt.Println("=== 查询特定Pod三小时内的成本明细 ===")
	
	request := &costsdk.CostV2Request{
		Window: "2024-03-24T00:00:00Z,2024-03-24T03:00:00Z",
		Step:   "1h",
		Filter: `namespace:"kube-system"+pod:"terway-eniip-kz68n"`,
	}
	
	ctx := context.Background()
	start := time.Now()
	response, err := client.CostV2.GetCostV2(ctx, request)
	if err != nil {
		fmt.Printf("查询Pod成本失败: %v\n", err)
		return
	}
	
	fmt.Printf("查询Pod成本耗时: %v\n", time.Since(start))
	
	for _, data := range response.Data {
		for name, cost := range data {
			fmt.Printf("Name: %s, Period: %s - %s, Cost: %.3f\n",
				name, cost.Start, cost.End, cost.Cost)
		}
	}
}

// 查询按标签聚合的成本
func queryAggregatedCost(client *costsdk.Client) {
	fmt.Println("=== 查询按标签聚合的成本 ===")
	
	idle := true
	request := &costsdk.CostV2Request{
		Window:    "2h",
		Aggregate: "label:app",
		Idle:      &idle,
	}
	
	ctx := context.Background()
	start := time.Now()
	response, err := client.CostV2.GetCostV2(ctx, request)
	if err != nil {
		fmt.Printf("查询聚合成本失败: %v\n", err)
		return
	}
	
	fmt.Printf("查询聚合成本耗时: %v\n", time.Since(start))
	
	for _, data := range response.Data {
		for name, cost := range data {
			fmt.Printf("App: %s, Cost: %.3f, CPU Usage: %.3f, Memory Usage: %.0f\n",
				name, cost.Cost, cost.CPUCoreUsageAverage, cost.RAMByteUsageAverage)
		}
	}
}