"""
Allocation API Service for Aliyun Container Service Cost SDK
"""

from typing import Optional, Dict, List
from urllib.parse import urlencode


class AllocationProperties:
    """Kubernetes object properties"""
    
    def __init__(self, pod: Optional[str] = None, node: Optional[str] = None, 
                 namespace: Optional[str] = None, controller_kind: Optional[str] = None,
                 controller: Optional[str] = None, provider_id: Optional[str] = None,
                 labels: Optional[Dict[str, str]] = None):
        self.pod = pod
        self.node = node
        self.namespace = namespace
        self.controller_kind = controller_kind
        self.controller = controller
        self.provider_id = provider_id
        self.labels = labels or {}


class AllocationData:
    """Allocation data"""
    
    def __init__(self, name: str, properties: AllocationProperties, start: str, end: str,
                 cpu_core_request_average: float, cpu_core_usage_average: float,
                 ram_byte_request_average: float, ram_byte_usage_average: float,
                 cost: float, cost_ratio: float, custom_cost: float):
        self.name = name
        self.properties = properties
        self.start = start
        self.end = end
        self.cpu_core_request_average = cpu_core_request_average
        self.cpu_core_usage_average = cpu_core_usage_average
        self.ram_byte_request_average = ram_byte_request_average
        self.ram_byte_usage_average = ram_byte_usage_average
        self.cost = cost
        self.cost_ratio = cost_ratio
        self.custom_cost = custom_cost


class AllocationResponse:
    """Allocation API response"""
    
    def __init__(self, data: List[Dict[str, AllocationData]]):
        self.data = data


class AllocationRequest:
    """Allocation API request parameters"""
    
    def __init__(self, window: str, filter: Optional[str] = None, step: Optional[str] = None,
                 aggregate: Optional[str] = None, idle: Optional[bool] = None,
                 share_idle: Optional[bool] = None, share_split: Optional[str] = None,
                 idle_by_node: Optional[bool] = None, target_type: Optional[str] = None,
                 format: Optional[str] = None):
        self.window = window
        self.filter = filter
        self.step = step
        self.aggregate = aggregate
        self.idle = idle
        self.share_idle = share_idle
        self.share_split = share_split
        self.idle_by_node = idle_by_node
        self.target_type = target_type
        self.format = format


class AllocationService:
    """Allocation API service"""
    
    def __init__(self, client):
        self.client = client
    
    def get_allocation(self, req: AllocationRequest) -> AllocationResponse:
        """
        Query Allocation cost data
        
        Args:
            req: AllocationRequest object
            
        Returns:
            AllocationResponse object
        """
        # Build URL
        api_url = f"{self.client.config.api_server}/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/v2/allocation"
        
        # Add query parameters
        params = {}
        params["window"] = req.window
        
        if req.filter:
            params["filter"] = req.filter
            
        if req.step:
            params["step"] = req.step
            
        if req.aggregate:
            params["aggregate"] = req.aggregate
            
        if req.idle is not None:
            params["idle"] = str(req.idle).lower()
            
        if req.share_idle is not None:
            params["shareIdle"] = str(req.share_idle).lower()
            
        if req.share_split:
            params["shareSplit"] = req.share_split
            
        if req.idle_by_node is not None:
            params["idleByNode"] = str(req.idle_by_node).lower()
            
        if req.target_type:
            params["targetType"] = req.target_type
            
        if req.format:
            params["format"] = req.format
        
        # Build complete URL
        if params:
            api_url = f"{api_url}?{urlencode(params)}"
        
        # Send request
        response = self.client.do_request("GET", api_url)
        
        # Check response status
        if response.status_code != 200:
            raise Exception(f"HTTP error {response.status_code}: {response.text}")
        
        # Parse response
        data = response.json()
        # In a real implementation, we would parse the JSON into our response objects
        # For now, we'll just return a basic response
        return AllocationResponse(data.get("data", []))