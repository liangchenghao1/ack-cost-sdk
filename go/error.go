package cost

import (
	"fmt"
	"net/http"
)

// Error 错误类型
type Error struct {
	Code    string
	Message string
	Details interface{}
}

// Error 实现error接口
func (e *Error) Error() string {
	return fmt.Sprintf("%s: %s", e.Code, e.Message)
}

// NewError 创建新的错误
func NewError(code, message string, details interface{}) *Error {
	return &Error{
		Code:    code,
		Message: message,
		Details: details,
	}
}

// 错误码定义
const (
	ErrCodeBadRequest     = "BadRequest"
	ErrCodeUnauthorized   = "Unauthorized"
	ErrCodeForbidden      = "Forbidden"
	ErrCodeNotFound       = "NotFound"
	ErrCodeTooManyRequests = "TooManyRequests"
	ErrCodeInternalError  = "InternalError"
	ErrCodeServiceUnavailable = "ServiceUnavailable"
)

// HTTPErrorFromStatusCode 根据HTTP状态码创建错误
func HTTPErrorFromStatusCode(statusCode int, details interface{}) *Error {
	var code string
	var message string
	
	switch statusCode {
	case http.StatusBadRequest:
		code = ErrCodeBadRequest
		message = "Bad Request"
	case http.StatusUnauthorized:
		code = ErrCodeUnauthorized
		message = "Unauthorized"
	case http.StatusForbidden:
		code = ErrCodeForbidden
		message = "Forbidden"
	case http.StatusNotFound:
		code = ErrCodeNotFound
		message = "Not Found"
	case http.StatusTooManyRequests:
		code = ErrCodeTooManyRequests
		message = "Too Many Requests"
	case http.StatusInternalServerError:
		code = ErrCodeInternalError
		message = "Internal Server Error"
	case http.StatusServiceUnavailable:
		code = ErrCodeServiceUnavailable
		message = "Service Unavailable"
	default:
		code = "UnknownError"
		message = fmt.Sprintf("Unknown error with status code %d", statusCode)
	}
	
	return NewError(code, message, details)
}