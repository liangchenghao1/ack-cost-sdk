package cost

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"net/url"
	"strconv"
)

// CostService Cost API服务
type CostService struct {
	client *Client
}

// CostMetadata 成本元数据
type CostMetadata struct {
	Timestamp     string `json:"timestamp"`
	TimeUnit      string `json:"timeUnit"`
	DimensionType string `json:"DimensionType"`
	Dimension     string `json:"Dimension"`
	PodName       string `json:"PodName"`
}

// CostResource 资源信息
type CostResource struct {
	CPU     float64 `json:"cpu"`
	Memory  float64 `json:"memory"`
	GPU     float64 `json:"gpu"`
	GPUMem  float64 `json:"gpuMem"`
}

// CostData 成本数据
type CostData struct {
	Metadata      CostMetadata `json:"metadata"`
	Request       CostResource `json:"request"`
	Usage         CostResource `json:"usage"`
	Limit         CostResource `json:"limit"`
	PerCorePricing float64     `json:"perCorePricing"`
	CostRatio     float64     `json:"costRatio"`
	Cost          float64     `json:"cost"`
	CustomCost    float64     `json:"customCost"`
}

// CostRequest Cost API请求参数
type CostRequest struct {
	// DimensionType 成本统计维度
	DimensionType string `url:"DimensionType,omitempty"`
	
	// Dimension 成本筛选值
	Dimension string `url:"Dimension,omitempty"`
	
	// LabelSelector 通过标签筛选Pod
	LabelSelector string `url:"LabelSelector,omitempty"`
	
	// TimeUnit 成本数据统计时间
	TimeUnit string `url:"TimeUnit,omitempty"`
	
	// Summary 是否返回数据总和
	Summary bool `url:"Summary,omitempty"`
}

// CostResponse Cost API响应
type CostResponse []CostData

// GetCost 查询成本数据
func (s *CostService) GetCost(ctx context.Context, req *CostRequest) (*CostResponse, error) {
	// 构建URL
	apiURL := fmt.Sprintf("%s/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/cost", s.client.config.APIServer)
	
	// 添加查询参数
	params := url.Values{}
	if req.DimensionType != "" {
		params.Add("DimensionType", req.DimensionType)
	}
	if req.Dimension != "" {
		params.Add("Dimension", req.Dimension)
	}
	if req.LabelSelector != "" {
		params.Add("LabelSelector", req.LabelSelector)
	}
	if req.TimeUnit != "" {
		params.Add("TimeUnit", req.TimeUnit)
	}
	if req.Summary {
		params.Add("Summary", strconv.FormatBool(req.Summary))
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
	var resp CostResponse
	if err := json.NewDecoder(httpResp.Body).Decode(&resp); err != nil {
		return nil, NewError(ErrCodeInternalError, "Failed to decode response", err)
	}
	
	return &resp, nil
}