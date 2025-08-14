"""
阿里云ACK成本管理Python SDK
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

这是一个用于查询阿里云ACK集群成本数据的Python SDK。

基本用法:

    from ack_cost_sdk import Client

    client = Client(api_server="https://kubernetes.default.svc")
    
    # 查询Cost V2数据
    response = client.cost_v2.get_cost_v2(window="yesterday")
    
    # 查询Allocation数据
    response = client.allocation.get_allocation(window="yesterday")

:copyright: (c) 2025 by Alibaba Cloud
:license: Apache 2.0, see LICENSE for more details.
"""

from .client import Client
from .costv2 import CostV2Service, CostV2Request, CostV2Response
from .allocation import AllocationService, AllocationRequest, AllocationResponse
from .errors import CostError, ERROR_CODES

__all__ = [
    "Client",
    "CostV2Service", "CostV2Request", "CostV2Response",
    "AllocationService", "AllocationRequest", "AllocationResponse",
    "CostError", "ERROR_CODES"
]

__title__ = "ack_cost_sdk"
__version__ = "1.0.0"
__build__ = 100
__author__ = "Alibaba Cloud"