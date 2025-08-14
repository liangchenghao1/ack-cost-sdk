"""
数据模型模块
"""

from typing import Dict, Optional, List
from dataclasses import dataclass, field


@dataclass
class Properties:
    """Kubernetes对象属性"""
    pod: Optional[str] = None
    node: Optional[str] = None
    namespace: Optional[str] = None
    controller_kind: Optional[str] = None
    controller: Optional[str] = None
    provider_id: Optional[str] = None
    labels: Dict[str, str] = field(default_factory=dict)


@dataclass
class CostData:
    """成本数据"""
    name: str = ""
    properties: Properties = field(default_factory=Properties)
    start: str = ""
    end: str = ""
    cpu_core_request_average: float = 0.0
    cpu_core_usage_average: float = 0.0
    ram_byte_request_average: float = 0.0
    ram_byte_usage_average: float = 0.0
    cost: float = 0.0
    cost_ratio: float = 0.0
    custom_cost: float = 0.0


@dataclass
class Response:
    """API响应基类"""
    data: List[Dict[str, CostData]] = field(default_factory=list)