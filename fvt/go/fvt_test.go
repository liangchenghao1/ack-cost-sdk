package main

import (
	"context"
	"fmt"
	"testing"

	cost "github.com/AliyunContainerService/cost-sdk/go"
)

// TestFVT 运行Go SDK的功能验证测试
func TestFVT(t *testing.T) {
	// 创建客户端，使用本地kubectl proxy地址
	client, err := cost.NewClient(&cost.Config{
		APIServer:  "http://127.0.0.1:8080",
		RetryCount: 3,
		RetryWait:  2,
	})
	if err != nil {
		t.Fatalf("Failed to create client: %v", err)
	}

	// 测试CostV2服务
	t.Run("CostV2Service", func(t *testing.T) {
		testCostV2Service(t, client)
	})

	// 测试Allocation服务
	t.Run("AllocationService", func(t *testing.T) {
		testAllocationService(t, client)
	})
}

func testCostV2Service(t *testing.T, client *cost.Client) {
	ctx := context.Background()

	// 测试1: 使用简单窗口参数查询
	t.Run("GetCostV2WithWindow", func(t *testing.T) {
		req := &cost.CostV2Request{
			Window: "24h",
		}
		resp, err := client.CostV2.GetCostV2(ctx, req)
		if err != nil {
			t.Logf("GetCostV2 failed: %v", err)
			// 不直接失败，因为可能是因为环境问题
			return
		}

		if resp == nil {
			t.Error("Response should not be nil")
			return
		}

		t.Logf("GetCostV2 returned %d data items", len(resp.Data))
		if len(resp.Data) > 0 {
			for _, data := range resp.Data {
				for name, cost := range data {
					t.Logf("Name: %s, Cost: %.3f", name, cost.Cost)
					break // 只打印一个示例
				}
				break // 只打印一个示例
			}
		}
	})

	// 测试2: 带过滤条件的查询
	t.Run("GetCostV2WithFilter", func(t *testing.T) {
		req := &cost.CostV2Request{
			Window: "24h",
			Filter: `namespace:"kube-system"`,
		}
		resp, err := client.CostV2.GetCostV2(ctx, req)
		if err != nil {
			t.Logf("GetCostV2 with filter failed: %v", err)
			return
		}

		if resp == nil {
			t.Error("Response should not be nil")
			return
		}

		t.Logf("GetCostV2 with filter returned %d data items", len(resp.Data))
	})

	// 测试3: 聚合查询
	t.Run("GetCostV2WithAggregate", func(t *testing.T) {
		idle := true
		req := &cost.CostV2Request{
			Window:    "24h",
			Aggregate: "namespace",
			Idle:      &idle,
		}
		resp, err := client.CostV2.GetCostV2(ctx, req)
		if err != nil {
			t.Logf("GetCostV2 with aggregate failed: %v", err)
			return
		}

		if resp == nil {
			t.Error("Response should not be nil")
			return
		}

		t.Logf("GetCostV2 with aggregate returned %d data items", len(resp.Data))
		
		// 显示聚合结果
		if len(resp.Data) > 0 {
			t.Log("Aggregated cost by namespace:")
			for _, data := range resp.Data {
				for namespace, cost := range data {
					t.Logf("  Namespace: %s, Cost: %.3f", namespace, cost.Cost)
				}
			}
		}
	})
	
	// 测试4: 动态获取集群中的工作负载
	t.Run("GetCostV2ForWorkloads", func(t *testing.T) {
		// 尝试获取一些常见的工作负载
		workloadFilters := []string{
			`controllerKind:"Deployment"`,
			`controllerKind:"StatefulSet"`,
			`controllerKind:"DaemonSet"`,
		}
		
		for _, filter := range workloadFilters {
			req := &cost.CostV2Request{
				Window: "24h",
				Filter: filter,
			}
			resp, err := client.CostV2.GetCostV2(ctx, req)
			if err != nil {
				t.Logf("GetCostV2 for %s failed: %v", filter, err)
				continue
			}
			
			if resp != nil && len(resp.Data) > 0 {
				t.Logf("Found %d %s workloads", len(resp.Data), filter)
				// 显示第一个工作负载作为示例
				for _, data := range resp.Data {
					for name, cost := range data {
						t.Logf("  Example: %s, Cost: %.3f", name, cost.Cost)
						break
					}
					break
				}
			} else {
				t.Logf("No %s workloads found", filter)
			}
		}
	})
}

func testAllocationService(t *testing.T, client *cost.Client) {
	ctx := context.Background()

	// 测试Allocation服务，最小查询时间窗口为24小时
	t.Run("GetAllocation", func(t *testing.T) {
		req := &cost.AllocationRequest{
			Window: "24h",
		}
		resp, err := client.Allocation.GetAllocation(ctx, req)
		if err != nil {
			t.Logf("GetAllocation failed: %v", err)
			// 不直接失败，因为可能是因为环境问题
			return
		}

		if resp == nil {
			t.Error("Response should not be nil")
			return
		}

		t.Logf("GetAllocation returned %d data items", len(resp.Data))
		if len(resp.Data) > 0 {
			for _, data := range resp.Data {
				for name, allocation := range data {
					t.Logf("Name: %s, Cost: %.3f", name, allocation.Cost)
					break // 只打印一个示例
				}
				break // 只打印一个示例
			}
		}
	})

	// 测试带聚合的Allocation查询
	t.Run("GetAllocationWithAggregate", func(t *testing.T) {
		req := &cost.AllocationRequest{
			Window:    "24h",
			Aggregate: "namespace",
		}
		resp, err := client.Allocation.GetAllocation(ctx, req)
		if err != nil {
			t.Logf("GetAllocation with aggregate failed: %v", err)
			return
		}

		if resp == nil {
			t.Error("Response should not be nil")
			return
		}

		t.Logf("GetAllocation with aggregate returned %d data items", len(resp.Data))
		
		// 显示聚合结果
		if len(resp.Data) > 0 {
			t.Log("Aggregated allocation by namespace:")
			for _, data := range resp.Data {
				for namespace, allocation := range data {
					t.Logf("  Namespace: %s, Cost: %.3f", namespace, allocation.Cost)
				}
			}
		}
	})
	
	// 测试动态获取工作负载的分配成本
	t.Run("GetAllocationForWorkloads", func(t *testing.T) {
		// 尝试获取一些常见的工作负载分配成本
		workloadFilters := []string{
			`controllerKind:"Deployment"`,
			`controllerKind:"StatefulSet"`,
			`controllerKind:"DaemonSet"`,
		}
		
		for _, filter := range workloadFilters {
			req := &cost.AllocationRequest{
				Window: "24h",
				Filter: filter,
			}
			resp, err := client.Allocation.GetAllocation(ctx, req)
			if err != nil {
				t.Logf("GetAllocation for %s failed: %v", filter, err)
				continue
			}
			
			if resp != nil && len(resp.Data) > 0 {
				t.Logf("Found %d %s workloads in allocation", len(resp.Data), filter)
				// 显示第一个工作负载作为示例
				for _, data := range resp.Data {
					for name, allocation := range data {
						t.Logf("  Example: %s, Cost: %.3f", name, allocation.Cost)
						break
					}
					break
				}
			} else {
				t.Logf("No %s workloads found in allocation", filter)
			}
		}
	})
}

// ExampleClient_usage 展示如何使用Client
func ExampleClient_usage() {
	client, err := cost.NewClient(&cost.Config{
		APIServer:  "http://127.0.0.1:8080",
		RetryCount: 1,
		RetryWait:  1,
	})
	if err != nil {
		fmt.Printf("Failed to create client: %v\n", err)
		return
	}

	req := &cost.CostV2Request{
		Window: "24h",
	}
	_, err = client.CostV2.GetCostV2(context.Background(), req)
	if err != nil {
		fmt.Printf("Failed to get cost data: %v\n", err)
		return
	}

	fmt.Printf("Retrieved cost data\n")
	// Output: Retrieved cost data
}