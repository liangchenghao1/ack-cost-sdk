"""
错误处理模块
"""

# 错误码定义
ERROR_CODES = {
    "BadRequest": "Bad Request",
    "Unauthorized": "Unauthorized",
    "Forbidden": "Forbidden",
    "NotFound": "Not Found",
    "TooManyRequests": "Too Many Requests",
    "InternalError": "Internal Server Error",
    "ServiceUnavailable": "Service Unavailable"
}


class CostError(Exception):
    """成本SDK基础错误类"""
    
    def __init__(self, code, message, details=None):
        self.code = code
        self.message = message
        self.details = details
        super().__init__(self.message)
    
    def __str__(self):
        return f"{self.code}: {self.message}"
    
    def __repr__(self):
        return f"CostError(code={self.code}, message={self.message}, details={self.details})"


def http_error_from_status_code(status_code, details=None):
    """根据HTTP状态码创建错误"""
    error_map = {
        400: ("BadRequest", "Bad Request"),
        401: ("Unauthorized", "Unauthorized"),
        403: ("Forbidden", "Forbidden"),
        404: ("NotFound", "Not Found"),
        429: ("TooManyRequests", "Too Many Requests"),
        500: ("InternalError", "Internal Server Error"),
        503: ("ServiceUnavailable", "Service Unavailable")
    }
    
    code, message = error_map.get(status_code, ("UnknownError", f"Unknown error with status code {status_code}"))
    return CostError(code, message, details)