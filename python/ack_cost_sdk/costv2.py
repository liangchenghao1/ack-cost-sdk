"""
Cost V2 API服务模块
"""

from typing import Optional, Dict, List
from urllib.parse import urlencode
from dataclasses import dataclass, field
import json
from .client import Client
from .models import Properties, CostData, Response
from .errors import CostError, http_error_from_status_code


@dataclass
class CostV2Request:
    """Cost V2 API请求参数"""
    window: str                                    # 查询的持续时间
    filter: Optional[str] = None                  # 资源过滤条件
    step: Optional[str] = None                    # 时间分段
    aggregate: Optional[str] = None               # 聚合维度
    idle: Optional[bool] = None                   # 是否展示闲置成本
    share_idle: Optional[bool] = None             # 是否分摊闲置成本
    share_split: Optional[str] = None             # 闲置分摊策略
    idle_by_node: Optional[bool] = None           # 是否按节点维度聚合闲置成本
    format: Optional[str] = None                  # 成本导出格式


@dataclass
class CostV2Data(CostData):
    """Cost V2 成本数据"""
    pass


@dataclass
class CostV2Response(Response):
    """Cost V2 API响应"""
    data: List[Dict[str, CostV2Data]] = field(default_factory=list)


class CostV2Service:
    """Cost V2 API服务"""
    
    def __init__(self, client: Client):
        self.client = client
    
    def get_cost_v2(self, request: CostV2Request) -> CostV2Response:
        """
        查询Cost V2成本数据
        
        Args:
            request: CostV2Request请求对象
            
        Returns:
            CostV2Response: 响应对象
            
        Raises:
            CostError: 请求失败或解析错误
        """
        # 构建URL
        api_url = f"{self.client.config.api_server}/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/v2/cost"
        
        # 添加查询参数
        params = {}
        params["window"] = request.window
        
        if request.filter:
            params["filter"] = request.filter
            
        if request.step:
            params["step"] = request.step
            
        if request.aggregate:
            params["aggregate"] = request.aggregate
            
        if request.idle is not None:
            params["idle"] = str(request.idle).lower()
            
        if request.share_idle is not None:
            params["shareIdle"] = str(request.share_idle).lower()
            
        if request.share_split:
            params["shareSplit"] = request.share_split
            
        if request.idle_by_node is not None:
            params["idleByNode"] = str(request.idle_by_node).lower()
            
        if request.format:
            params["format"] = request.format
        
        # 构建完整URL
        if params:
            api_url = f"{api_url}?{urlencode(params)}"
        
        try:
            # 发送请求
            response = self.client.do_request("GET", api_url)
            
            # 检查响应状态码
            if response.status_code != 200:
                raise http_error_from_status_code(response.status_code)
            
            # 解析响应
            raw_data = response.json()
            result = CostV2Response()
            
            # 转换数据格式
            for item in raw_data.get("data", []):
                data_dict = {}
                for key, value in item.items():
                    cost_data = CostV2Data()
                    cost_data.name = value.get("name", "")
                    
                    # 处理properties
                    props = value.get("properties", {})
                    cost_data.properties = Properties(
                        pod=props.get("pod"),
                        node=props.get("node"),
                        namespace=props.get("namespace"),
                        controller_kind=props.get("controllerKind"),
                        controller=props.get("controller"),
                        provider_id=props.get("providerID"),
                        labels=props.get("labels", {})
                    )
                    
                    cost_data.start = value.get("start", "")
                    cost_data.end = value.get("end", "")
                    cost_data.cpu_core_request_average = value.get("cpuCoreRequestAverage", 0.0)
                    cost_data.cpu_core_usage_average = value.get("cpuCoreUsageAverage", 0.0)
                    cost_data.ram_byte_request_average = value.get("ramByteRequestAverage", 0.0)
                    cost_data.ram_byte_usage_average = value.get("ramByteUsageAverage", 0.0)
                    cost_data.cost = value.get("cost", 0.0)
                    cost_data.cost_ratio = value.get("costRatio", 0.0)
                    cost_data.custom_cost = value.get("customCost", 0.0)
                    
                    data_dict[key] = cost_data
                result.data.append(data_dict)
            
            return result
            
        except json.JSONDecodeError as e:
            raise CostError("InternalError", "Failed to decode response", e)
        except Exception as e:
            if isinstance(e, CostError):
                raise e
            raise CostError("InternalError", "Failed to send request", e)