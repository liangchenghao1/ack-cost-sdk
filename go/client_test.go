package cost

import (
	"testing"
)

func TestNewClient(t *testing.T) {
	// 测试默认配置
	client, err := NewClient(nil)
	if err != nil {
		t.Fatalf("Failed to create client with nil config: %v", err)
	}

	if client.config.Timeout != 30 {
		t.Errorf("Expected default timeout to be 30, got %d", client.config.Timeout)
	}

	if client.config.RetryCount != 3 {
		t.Errorf("Expected default retry count to be 3, got %d", client.config.RetryCount)
	}

	if client.config.RetryWait != 1 {
		t.Errorf("Expected default retry wait to be 1, got %d", client.config.RetryWait)
	}

	// 测试自定义配置
	config := &Config{
		APIServer:  "https://test.example.com",
		Timeout:    60,
		RetryCount: 5,
		RetryWait:  2,
	}

	client, err = NewClient(config)
	if err != nil {
		t.Fatalf("Failed to create client with custom config: %v", err)
	}

	if client.config.Timeout != 60 {
		t.Errorf("Expected timeout to be 60, got %d", client.config.Timeout)
	}

	if client.config.RetryCount != 5 {
		t.Errorf("Expected retry count to be 5, got %d", client.config.RetryCount)
	}

	if client.config.RetryWait != 2 {
		t.Errorf("Expected retry wait to be 2, got %d", client.config.RetryWait)
	}
}

func TestError_Error(t *testing.T) {
	err := &Error{
		Code:    "TestError",
		Message: "This is a test error",
	}

	expected := "TestError: This is a test error"
	if err.Error() != expected {
		t.Errorf("Expected error string to be %s, got %s", expected, err.Error())
	}
}

func TestHTTPErrorFromStatusCode(t *testing.T) {
	testCases := []struct {
		statusCode int
		code       string
	}{
		{400, ErrCodeBadRequest},
		{401, ErrCodeUnauthorized},
		{403, ErrCodeForbidden},
		{404, ErrCodeNotFound},
		{429, ErrCodeTooManyRequests},
		{500, ErrCodeInternalError},
		{503, ErrCodeServiceUnavailable},
		{999, "UnknownError"}, // 未知状态码
	}

	for _, tc := range testCases {
		err := HTTPErrorFromStatusCode(tc.statusCode, nil)
		if err.Code != tc.code {
			t.Errorf("Expected error code to be %s for status %d, got %s", tc.code, tc.statusCode, err.Code)
		}
	}
}

func TestCostRequest(t *testing.T) {
	req := &CostRequest{
		DimensionType: "Namespace",
		Dimension:     "default",
		TimeUnit:      "day",
		Summary:       true,
	}

	if req.DimensionType != "Namespace" {
		t.Errorf("Expected DimensionType to be Namespace, got %s", req.DimensionType)
	}

	if req.Dimension != "default" {
		t.Errorf("Expected Dimension to be default, got %s", req.Dimension)
	}

	if req.TimeUnit != "day" {
		t.Errorf("Expected TimeUnit to be day, got %s", req.TimeUnit)
	}

	if !req.Summary {
		t.Error("Expected Summary to be true")
	}
}

func TestCostV2Request(t *testing.T) {
	idle := true
	req := &CostV2Request{
		Window:    "yesterday",
		Filter:    `namespace:"default"`,
		Step:      "1h",
		Aggregate: "namespace",
		Idle:      &idle,
	}

	if req.Window != "yesterday" {
		t.Errorf("Expected Window to be yesterday, got %s", req.Window)
	}

	if req.Filter != `namespace:"default"` {
		t.Errorf("Expected Filter to be namespace:\"default\", got %s", req.Filter)
	}

	if req.Step != "1h" {
		t.Errorf("Expected Step to be 1h, got %s", req.Step)
	}

	if req.Aggregate != "namespace" {
		t.Errorf("Expected Aggregate to be namespace, got %s", req.Aggregate)
	}

	if req.Idle == nil || *req.Idle != true {
		t.Error("Expected Idle to be true")
	}
}

func TestAllocationRequest(t *testing.T) {
	idle := false
	req := &AllocationRequest{
		Window:     "yesterday",
		Filter:     `namespace:"default"`,
		Step:       "1h",
		Aggregate:  "namespace",
		Idle:       &idle,
		TargetType: "cluster",
	}

	if req.Window != "yesterday" {
		t.Errorf("Expected Window to be yesterday, got %s", req.Window)
	}

	if req.Filter != `namespace:"default"` {
		t.Errorf("Expected Filter to be namespace:\"default\", got %s", req.Filter)
	}

	if req.Step != "1h" {
		t.Errorf("Expected Step to be 1h, got %s", req.Step)
	}

	if req.Aggregate != "namespace" {
		t.Errorf("Expected Aggregate to be namespace, got %s", req.Aggregate)
	}

	if req.Idle == nil || *req.Idle != false {
		t.Error("Expected Idle to be false")
	}

	if req.TargetType != "cluster" {
		t.Errorf("Expected TargetType to be cluster, got %s", req.TargetType)
	}
}