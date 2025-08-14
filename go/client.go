package cost

import (
	"context"
	"net/http"
	"time"
)

// Config 客户端配置
type Config struct {
	// APIServer Kubernetes API Server地址
	APIServer string
	
	// Timeout 请求超时时间(秒)
	Timeout int
	
	// RetryCount 重试次数
	RetryCount int
	
	// RetryWait 重试间隔(秒)
	RetryWait int
	
	// HTTPClient 自定义HTTP客户端
	HTTPClient *http.Client
}

// Client SDK客户端
type Client struct {
	config     *Config
	httpClient *http.Client
	Cost       *CostService
	CostV2     *CostV2Service
	Allocation *AllocationService
}

// NewClient 创建新的客户端实例
func NewClient(config *Config) (*Client, error) {
	if config == nil {
		config = &Config{}
	}
	
	// 设置默认值
	if config.Timeout == 0 {
		config.Timeout = 30
	}
	
	if config.RetryCount == 0 {
		config.RetryCount = 3
	}
	
	if config.RetryWait == 0 {
		config.RetryWait = 1
	}
	
	// 创建HTTP客户端
	httpClient := config.HTTPClient
	if httpClient == nil {
		httpClient = &http.Client{
			Timeout: time.Duration(config.Timeout) * time.Second,
		}
	}
	
	client := &Client{
		config:     config,
		httpClient: httpClient,
	}
	
	// 初始化各服务
	client.Cost = &CostService{client: client}
	client.CostV2 = &CostV2Service{client: client}
	client.Allocation = &AllocationService{client: client}
	
	return client, nil
}

// Do 发送HTTP请求
func (c *Client) Do(ctx context.Context, req *http.Request) (*http.Response, error) {
	// 添加context到请求
	req = req.WithContext(ctx)
	
	var resp *http.Response
	var err error
	
	// 重试机制
	for i := 0; i <= c.config.RetryCount; i++ {
		resp, err = c.httpClient.Do(req)
		if err == nil {
			break
		}
		
		// 如果不是最后一次重试，等待一段时间后重试
		if i < c.config.RetryCount {
			time.Sleep(time.Duration(c.config.RetryWait) * time.Second)
		}
	}
	
	return resp, err
}