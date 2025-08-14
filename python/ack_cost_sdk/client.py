"""
客户端模块
"""

import requests
import time
from typing import Optional
from .costv2 import CostV2Service
from .allocation import AllocationService


class Config:
    """客户端配置"""
    
    def __init__(self, 
                 api_server: str = "",
                 timeout: int = 30,
                 retry_count: int = 3,
                 retry_wait: int = 1):
        self.api_server = api_server
        self.timeout = timeout
        self.retry_count = retry_count
        self.retry_wait = retry_wait


class Client:
    """SDK客户端"""
    
    def __init__(self, config: Optional[Config] = None):
        if config is None:
            config = Config()
        
        self.config = config
        
        # 创建HTTP客户端
        self.session = requests.Session()
        self.session.timeout = config.timeout
        
        # 初始化各服务
        self.cost_v2 = CostV2Service(self)
        self.allocation = AllocationService(self)
    
    def do_request(self, method: str, url: str, **kwargs) -> requests.Response:
        """
        发送HTTP请求，带重试机制
        
        Args:
            method: HTTP方法
            url: 请求URL
            **kwargs: 其他请求参数
            
        Returns:
            requests.Response: HTTP响应对象
            
        Raises:
            CostError: 请求失败
        """
        # 添加超时设置
        if 'timeout' not in kwargs:
            kwargs['timeout'] = self.config.timeout
            
        last_exception = None
        
        # 重试机制
        for i in range(self.config.retry_count + 1):
            try:
                response = self.session.request(method, url, **kwargs)
                return response
            except Exception as e:
                last_exception = e
                # 如果不是最后一次重试，等待一段时间后重试
                if i < self.config.retry_count:
                    time.sleep(self.config.retry_wait)
        
        # 如果所有重试都失败，抛出异常
        raise last_exception