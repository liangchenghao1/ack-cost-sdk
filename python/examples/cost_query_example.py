#!/usr/bin/env python3
# coding: utf-8

"""
Cost Query Example
"""

import json
import sys
import os

# 添加父目录到Python路径，以便导入openapi_client
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from openapi_client import ApiClient, DefaultApi


def query_cost_data():
    """查询成本数据"""
    print("Starting cost data query...")
    
    try:
        # 创建API客户端，使用kubeconfig和Kubernetes服务代理
        proxy_path = "/api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy"
        api_client = ApiClient.new_api_client_with_kubeconfig_and_proxy(proxy_path=proxy_path)
        
        print(f"Client created successfully!")
        print(f"Server URL: {api_client.configuration.host}")
        
        # 创建API实例
        default_api = DefaultApi(api_client)
        print(f"API instance created successfully")
        
        # 查询成本数据 - 查询kube-system命名空间中的ack-cost-exporter
        print(f"Querying cost data for ack-cost-exporter in kube-system namespace...")
        
        # 使用直接HTTP调用，避免模型解析问题
        url = f"{api_client.configuration.host}/v2/cost"
        params = {
            'window': '1h',
            'filter': 'namespace:"kube-system"+controllerKind:"ReplicaSet"+label[app]:"ack-cost-exporter"'
        }
        
        # 构建查询URL
        query_string = "&".join([f"{k}={v}" for k, v in params.items()])
        full_url = f"{url}?{query_string}"
        
        print(f"Request URL: {full_url}")
        
        # 发送HTTP请求
        response = api_client.rest_client.request(
            method='GET',
            url=full_url,
            headers={'Accept': 'application/json'}
        )
        
        print(f"HTTP Status Code: {response.status}")
        
        # 读取响应数据
        response_data = response.read()
        
        if response_data:
            try:
                # 解析JSON响应
                json_data = json.loads(response_data.decode('utf-8'))
                
                print(f"Successfully retrieved cost data")
                print(f"Response data structure:")
                
                if 'data' in json_data and json_data['data']:
                    data = json_data['data']
                    
                    # 处理不同的数据结构
                    if isinstance(data, list):
                        total_cost = 0
                        total_resources = 0
                        
                        for i, data_item in enumerate(data):
                            print(f"\n   Time Range {i+1}:")
                            if isinstance(data_item, dict):
                                for resource_name, allocation in data_item.items():
                                    total_resources += 1
                                    cost = allocation.get('cost', 0)
                                    total_cost += cost
                                    
                                    print(f"\n     Pod: {resource_name}")
                                    
                                    # Display basic allocation info
                                    if allocation.get('name'):
                                        print(f"       Name: {allocation['name']}")
                                    if allocation.get('start'):
                                        print(f"       Start: {allocation['start']}")
                                    if allocation.get('end'):
                                        print(f"       End: {allocation['end']}")
                                    
                                    # Display resource usage
                                    if allocation.get('cpuCoreRequestAverage'):
                                        print(f"       CPU Request Average: {allocation['cpuCoreRequestAverage']:.2f} cores")
                                    if allocation.get('cpuCoreUsageAverage'):
                                        print(f"       CPU Usage Average: {allocation['cpuCoreUsageAverage']:.2f} cores")
                                    if allocation.get('ramByteRequestAverage'):
                                        print(f"       RAM Request Average: {allocation['ramByteRequestAverage']/1024/1024:.2f} MB")
                                    if allocation.get('ramByteUsageAverage'):
                                        print(f"       RAM Usage Average: {allocation['ramByteUsageAverage']/1024/1024:.2f} MB")
                                    
                                    # Display cost information
                                    if allocation.get('cost'):
                                        print(f"       Cost: {allocation['cost']:.4f}")
                                    if allocation.get('costRatio'):
                                        print(f"       Cost Ratio: {allocation['costRatio']*100:.2f}%")
                                    if allocation.get('customCost'):
                                        print(f"       Custom Cost: {allocation['customCost']:.4f}")
                                    
                                    # Display properties if available
                                    if allocation.get('properties'):
                                        props = allocation['properties']
                                        print(f"       Properties:")
                                        if props.get('namespace'):
                                            print(f"         Namespace: {props['namespace']}")
                                        if props.get('node'):
                                            print(f"         Node: {props['node']}")
                                        if props.get('controller'):
                                            print(f"         Controller: {props['controller']}")
                                        if props.get('controllerKind'):
                                            print(f"         Controller Kind: {props['controllerKind']}")
                                        if props.get('cluster'):
                                            print(f"         Cluster: {props['cluster']}")
                        
                        print(f"\nTotal: {total_resources} resource allocations")
                        print(f"Total Cost: {total_cost:.6f}")
                    elif isinstance(data, dict):
                        print(f"   - Found {len(data)} resource allocations")
                        total_cost = 0
                        for resource_name, allocation in data.items():
                            cost = allocation.get('cost', 0)
                            total_cost += cost
                            
                            print(f"\n     Pod: {resource_name}")
                            
                            # Display basic allocation info
                            if allocation.get('name'):
                                print(f"       Name: {allocation['name']}")
                            if allocation.get('start'):
                                print(f"       Start: {allocation['start']}")
                            if allocation.get('end'):
                                print(f"       End: {allocation['end']}")
                            
                            # Display resource usage
                            if allocation.get('cpuCoreRequestAverage'):
                                print(f"       CPU Request Average: {allocation['cpuCoreRequestAverage']:.2f} cores")
                            if allocation.get('cpuCoreUsageAverage'):
                                print(f"       CPU Usage Average: {allocation['cpuCoreUsageAverage']:.2f} cores")
                            if allocation.get('ramByteRequestAverage'):
                                print(f"       RAM Request Average: {allocation['ramByteRequestAverage']/1024/1024:.2f} MB")
                            if allocation.get('ramByteUsageAverage'):
                                print(f"       RAM Usage Average: {allocation['ramByteUsageAverage']/1024/1024:.2f} MB")
                            
                            # Display cost information
                            if allocation.get('cost'):
                                print(f"       Cost: {allocation['cost']:.4f}")
                            if allocation.get('costRatio'):
                                print(f"       Cost Ratio: {allocation['costRatio']*100:.2f}%")
                            if allocation.get('customCost'):
                                print(f"       Custom Cost: {allocation['customCost']:.4f}")
                            
                            # Display properties if available
                            if allocation.get('properties'):
                                props = allocation['properties']
                                print(f"       Properties:")
                                if props.get('namespace'):
                                    print(f"         Namespace: {props['namespace']}")
                                if props.get('node'):
                                    print(f"         Node: {props['node']}")
                                if props.get('controller'):
                                    print(f"         Controller: {props['controller']}")
                                if props.get('controllerKind'):
                                    print(f"         Controller Kind: {props['controllerKind']}")
                                if props.get('cluster'):
                                    print(f"         Cluster: {props['cluster']}")
                        
                        print(f"\nTotal Cost: {total_cost:.6f}")
                    else:
                        print(f"   - Unknown data structure: {type(data)}")
                        print(f"   - Data content: {data}")
                else:
                    print(f"   - No matching resources found")
                    print(f"   - Raw response: {json_data}")
            except json.JSONDecodeError as e:
                print(f"JSON parsing failed: {e}")
                print(f"Raw response: {response_data.decode('utf-8')}")
        else:
            print(f"Received empty response")
        
        return True
        
    except Exception as e:
        print(f"Query failed: {e}")
        import traceback
        traceback.print_exc()
        return False


def main():
    """主函数"""
    print("=" * 60)
    print("Python SDK Kubeconfig Cost Query Example")
    print("=" * 60)
    
    # 检查kubeconfig是否存在
    kubeconfig_paths = []
    
    # 检查KUBECONFIG环境变量
    kubeconfig_env = os.environ.get('KUBECONFIG')
    if kubeconfig_env and os.path.exists(kubeconfig_env):
        kubeconfig_paths.append(kubeconfig_env)
    
    # 检查默认位置
    home_dir = os.path.expanduser('~')
    default_path = os.path.join(home_dir, '.kube', 'config')
    if os.path.exists(default_path):
        kubeconfig_paths.append(default_path)
    
    if not kubeconfig_paths:
        print("No kubeconfig file found")
        print("Please ensure:")
        print("  1. KUBECONFIG environment variable is set, or")
        print("  2. kubeconfig file exists at ~/.kube/config")
        return 1
    
    print(f"Found kubeconfig file: {kubeconfig_paths[0]}")
    
    # 执行成本查询
    success = query_cost_data()
    
    print("=" * 60)
    if success:
        print("Example completed successfully!")
    else:
        print("Example execution failed")
    
    return 0 if success else 1


if __name__ == "__main__":
    exit(main())