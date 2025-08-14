package cost

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"net/url"
	"strconv"
)

// AllocationService Allocation API服务
type AllocationService struct {
	client *Client
}

// AllocationProperties Kubernetes对象属性
type AllocationProperties struct {
	Pod            string            `json:"pod,omitempty"`
	Node           string            `json:"node,omitempty"`
	Namespace      string            `json:"namespace,omitempty"`
	ControllerKind string            `json:"controllerKind,omitempty"`
	Controller     string            `json:"controller,omitempty"`
	ProviderID     string            `json:"providerID,omitempty"`
	Labels         map[string]string `json:"labels,omitempty"`
}

// AllocationData 分摊成本数据
type AllocationData struct {
	Name                 string              `json:"name"`
	Properties           AllocationProperties `json:"properties"`
	Start                string               `json:"start"`
	End                  string               `json:"end"`
	CPUCoreRequestAverage float64            `json:"cpuCoreRequestAverage"`
	CPUCoreUsageAverage  float64             `json:"cpuCoreUsageAverage"`
	RAMByteRequestAverage float64           `json:"ramByteRequestAverage"`
	RAMByteUsageAverage  float64             `json:"ramByteUsageAverage"`
	Cost                 float64             `json:"cost"`
	CostRatio            float64             `json:"costRatio"`
	CustomCost           float64             `json:"customCost"`
}

// AllocationResponse Allocation API响应
type AllocationResponse struct {
	Data []map[string]*AllocationData `json:"data"`
}

// AllocationRequest Allocation API请求参数
type AllocationRequest struct {
	// Window 查询的持续时间
	Window string `url:"window"`
	
	// Filter 资源过滤条件
	Filter string `url:"filter,omitempty"`
	
	// Step 时间分段
	Step string `url:"step,omitempty"`
	
	// Aggregate 聚合维度
	Aggregate string `url:"aggregate,omitempty"`
	
	// Idle 是否展示闲置成本
	Idle *bool `url:"idle,omitempty"`
	
	// ShareIdle 是否分摊闲置成本
	ShareIdle *bool `url:"shareIdle,omitempty"`
	
	// ShareSplit 闲置分摊策略
	ShareSplit string `url:"shareSplit,omitempty"`
	
	// IdleByNode 是否按节点维度聚合闲置成本
	IdleByNode *bool `url:"idleByNode,omitempty"`
	
	// TargetType 成本分摊的目标类型
	TargetType string `url:"targetType,omitempty"`
	
	// Format 成本导出格式
	Format string `url:"format,omitempty"`
}

// GetAllocation 查询Allocation成本数据
func (s *AllocationService) GetAllocation(ctx context.Context, req *AllocationRequest) (*AllocationResponse, error) {
	// 构建URL
	apiURL := fmt.Sprintf("%s/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/v2/allocation", s.client.config.APIServer)
	
	// 添加查询参数
	params := url.Values{}
	params.Add("window", req.Window)
	
	if req.Filter != "" {
		params.Add("filter", req.Filter)
	}
	
	if req.Step != "" {
		params.Add("step", req.Step)
	}
	
	if req.Aggregate != "" {
		params.Add("aggregate", req.Aggregate)
	}
	
	if req.Idle != nil {
		params.Add("idle", strconv.FormatBool(*req.Idle))
	}
	
	if req.ShareIdle != nil {
		params.Add("shareIdle", strconv.FormatBool(*req.ShareIdle))
	}
	
	if req.ShareSplit != "" {
		params.Add("shareSplit", req.ShareSplit)
	}
	
	if req.IdleByNode != nil {
		params.Add("idleByNode", strconv.FormatBool(*req.IdleByNode))
	}
	
	if req.TargetType != "" {
		params.Add("targetType", req.TargetType)
	}
	
	if req.Format != "" {
		params.Add("format", req.Format)
	}
	
	// 构建完整URL
	if len(params) > 0 {
		apiURL = fmt.Sprintf("%s?%s", apiURL, params.Encode())
	}
	
	// 创建HTTP请求
	httpReq, err := http.NewRequest("GET", apiURL, nil)
	if err != nil {
		return nil, NewError(ErrCodeBadRequest, "Failed to create request", err)
	}
	
	// 发送请求
	httpResp, err := s.client.Do(ctx, httpReq)
	if err != nil {
		return nil, NewError(ErrCodeInternalError, "Failed to send request", err)
	}
	defer httpResp.Body.Close()
	
	// 检查响应状态码
	if httpResp.StatusCode != http.StatusOK {
		return nil, HTTPErrorFromStatusCode(httpResp.StatusCode, nil)
	}
	
	// 解析响应
	var resp AllocationResponse
	if err := json.NewDecoder(httpResp.Body).Decode(&resp); err != nil {
		return nil, NewError(ErrCodeInternalError, "Failed to decode response", err)
	}
	
	return &resp, nil
}