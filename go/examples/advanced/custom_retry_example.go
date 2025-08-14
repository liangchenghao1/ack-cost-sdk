package main

import (
	"context"
	"fmt"
	"net/http"
	"time"

	costsdk "github.com/AliyunContainerService/cost-sdk/go"
)

// 自定义HTTP客户端示例
func main() {
	// 创建自定义HTTP客户端，设置特定的超时和传输配置
	customHTTPClient := &http.Client{
		Timeout: 60 * time.Second,
		Transport: &http.Transport{
			MaxIdleConns:        100,
			MaxIdleConnsPerHost: 10,
			IdleConnTimeout:     30 * time.Second,
		},
	}

	// 创建客户端，使用自定义HTTP客户端
	client, err := costsdk.NewClient(&costsdk.Config{
		APIServer:  "https://kubernetes.default.svc",
		Timeout:    60,
		RetryCount: 5,
		RetryWait:  3,
		HTTPClient: customHTTPClient,
	})
	if err != nil {
		panic(err)
	}

	// 查询DaemonSet昨天的成本
	fmt.Println("=== 使用自定义重试策略查询成本 ===")
	
	request := &costsdk.CostV2Request{
		Window: "yesterday",
		Filter: `namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"`,
	}
	
	ctx := context.Background()
	response, err := client.CostV2.GetCostV2(ctx, request)
	if err != nil {
		fmt.Printf("查询成本失败: %v\n", err)
		return
	}
	
	for _, data := range response.Data {
		for name, cost := range data {
			fmt.Printf("Name: %s, Cost: %.3f\n", name, cost.Cost)
		}
	}
}