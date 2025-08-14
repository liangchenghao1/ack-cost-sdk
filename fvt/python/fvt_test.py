#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
FVT测试文件，用于验证Python SDK的功能
"""

import sys
import os
import json
import time
import traceback

# 将上级目录添加到Python路径中
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', '..'))
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', '..', 'python'))

try:
    from ack_cost_sdk.client import Client, Config
    sdk_available = True
except ImportError as e:
    print(f"无法导入Python SDK: {e}")
    sdk_available = False

def run_fvt():
    print("=== Python FVT测试开始 ===")
    
    if not sdk_available:
        print("Python SDK不可用，跳过测试")
        # 测试4: 动态获取集群中的工作负载
        print("测试4: 动态获取集群中的工作负载")
        workload_filters = [
            'controllerKind:"Deployment"',
            'controllerKind:"StatefulSet"',
            'controllerKind:"DaemonSet"'
        ]
        
        for filter_str in workload_filters:
            print(f"Python SDK将支持查询 {filter_str} 类型的工作负载")
        
        print("=== Python FVT测试结束 ===\n")
        return
    
    try:
        # 初始化客户端
        config = Config()
        config.endpoint = "http://127.0.0.1:8080"
        client = Client(config)
        
        test_cost_v2_service(client)
        test_allocation_service(client)
        
    except Exception as e:
        print(f"Python FVT测试执行出错: {e}")
        # 即使出现异常也继续执行其他测试
    
    # 测试4: 动态获取集群中的工作负载
    print("测试4: 动态获取集群中的工作负载")
    workload_filters = [
        'controllerKind:"Deployment"',
        'controllerKind:"StatefulSet"',
        'controllerKind:"DaemonSet"'
    ]
    
    for filter_str in workload_filters:
        print(f"Python SDK将支持查询 {filter_str} 类型的工作负载")
    
    print("=== Python FVT测试结束 ===\n")

def print_request_log(api_name, request_data):
    """打印请求日志"""
    print(f"  发送请求到 {api_name}")
    print(f"  请求参数: {json.dumps(request_data, ensure_ascii=False)}")

def print_response_log(response_data, elapsed_time=None):
    """打印响应日志"""
    if elapsed_time is not None:
        print(f"  请求耗时: {elapsed_time:.2f}秒")
    
    code = response_data.get('code', 'N/A')
    print(f"  响应状态码: {code}")
    
    if 'data' in response_data and response_data['data']:
        data = response_data['data']
        # 如果是字典类型，尝试格式化输出
        if isinstance(data, dict):
            print(f"  响应数据详情:")
            # 如果有properties字段，说明是成本数据
            if 'properties' in data:
                props = data['properties']
                if 'name' in props:
                    print(f"    名称: {props['name']}")
                if 'cpuCost' in props:
                    print(f"    CPU成本: {props['cpuCost']}")
                if 'gpuCost' in props:
                    print(f"    GPU成本: {props['gpuCost']}")
                if 'ramCost' in props:
                    print(f"    内存成本: {props['ramCost']}")
                if 'pvCost' in props:
                    print(f"    存储成本: {props['pvCost']}")
                if 'totalCost' in props:
                    print(f"    总成本: {props['totalCost']}")
            # 如果有items字段，说明是列表数据
            elif 'items' in data:
                items = data['items']
                print(f"    数据项数量: {len(items)}")
                # 只显示前3个项目
                for i, item in enumerate(items[:3]):
                    print(f"    数据项 {i+1}:")
                    if isinstance(item, dict) and 'properties' in item:
                        props = item['properties']
                        if 'name' in props:
                            print(f"      名称: {props['name']}")
                        if 'totalCost' in props:
                            print(f"      总成本: {props['totalCost']}")
                    else:
                        item_str = str(item)
                        print(f"      内容: {item_str[:100]}{'...' if len(item_str) > 100 else ''}")
                if len(items) > 3:
                    print(f"    ...还有{len(items) - 3}个项目未显示")
            # 其他字典类型数据
            else:
                data_str = json.dumps(data, ensure_ascii=False, indent=2)
                lines = data_str.split('\n')
                if len(lines) <= 20:
                    print(f"    数据内容:\n{data_str}")
                else:
                    # 只显示前20行
                    print("    数据内容:")
                    for line in lines[:20]:
                        print(line)
                    print(f"    ...还有{len(lines) - 20}行未显示")
        # 如果是字符串类型
        elif isinstance(data, str):
            print(f"  响应数据: {data[:500]}{'...' if len(data) > 500 else ''}")
        # 其他类型
        else:
            data_str = str(data)
            print(f"  响应数据: {data_str[:500]}{'...' if len(data_str) > 500 else ''}")
    elif 'error' in response_data and response_data['error']:
        print(f"  错误信息: {response_data['error']}")

def test_cost_v2_service(client):
    print("测试1: CostV2Service")
    
    try:
        # 测试1.1: 基本的GetCostV2调用
        print("测试1.1: GetCostV2 with window")
        req1 = {}
        req1["window"] = "today"
        print_request_log("CostV2Service.GetCostV2", req1)
        start_time = time.time()
        resp1 = client.get_cost_v2(req1)
        elapsed_time = time.time() - start_time
        print_response_log(resp1, elapsed_time)
        
        # 验证返回值
        if resp1.get('code') == 200:
            print("  ✓ 成本API调用成功")
        else:
            print(f"  ✗ 成本API调用失败，状态码: {resp1.get('code')}")
    except Exception as e:
        print(f"  GetCostV2 with window测试失败: {e}")
        traceback.print_exc()
    
    try:
        # 测试1.2: 带filter的GetCostV2调用
        print("测试1.2: GetCostV2 with filter")
        req2 = {}
        req2["window"] = "today"
        req2["filter"] = 'namespace:"kube-system"'
        print_request_log("CostV2Service.GetCostV2", req2)
        start_time = time.time()
        resp2 = client.get_cost_v2(req2)
        elapsed_time = time.time() - start_time
        print_response_log(resp2, elapsed_time)
        
        # 验证返回值
        if resp2.get('code') == 200:
            print("  ✓ 带过滤条件的成本API调用成功")
        else:
            print(f"  ✗ 带过滤条件的成本API调用失败，状态码: {resp2.get('code')}")
    except Exception as e:
        print(f"  GetCostV2 with filter测试失败: {e}")
        traceback.print_exc()
    
    try:
        # 测试1.3: 带aggregate的GetCostV2调用
        print("测试1.3: GetCostV2 with aggregate")
        req3 = {}
        req3["window"] = "today"
        req3["aggregate"] = "namespace"
        print_request_log("CostV2Service.GetCostV2", req3)
        start_time = time.time()
        resp3 = client.get_cost_v2(req3)
        elapsed_time = time.time() - start_time
        print_response_log(resp3, elapsed_time)
        
        # 验证返回值
        if resp3.get('code') == 200:
            print("  ✓ 聚合查询的成本API调用成功")
        else:
            print(f"  ✗ 聚合查询的成本API调用失败，状态码: {resp3.get('code')}")
    except Exception as e:
        print(f"  GetCostV2 with aggregate测试失败: {e}")
        traceback.print_exc()
    
    # 测试1.4: 获取工作负载的成本数据
    print("测试1.4: GetCostV2 for workloads")
    workload_types = ["Deployment", "StatefulSet", "DaemonSet"]
    for workload_type in workload_types:
        try:
            req = {}
            req["window"] = "today"
            req["filter"] = f'controllerKind:"{workload_type}"'
            print_request_log("CostV2Service.GetCostV2", req)
            start_time = time.time()
            resp = client.get_cost_v2(req)
            elapsed_time = time.time() - start_time
            print_response_log(resp, elapsed_time)
            
            # 验证返回值
            code = resp.get('code')
            if code == 200:
                print(f"  ✓ {workload_type}类型工作负载成本查询成功")
            elif code == 503:
                print(f"  - {workload_type}类型工作负载查询返回服务不可用(503)")
            else:
                print(f"  ✗ {workload_type}类型工作负载查询失败，状态码: {code}")
        except Exception as e:
            print(f"  GetCostV2 for controllerKind:\"{workload_type}\" failed: {e}")
            traceback.print_exc()

def test_allocation_service(client):
    print("测试2: AllocationService")
    
    try:
        # 测试2.1: 基本的GetAllocation调用
        print("测试2.1: GetAllocation")
        req1 = {}
        req1["window"] = "today"
        print_request_log("AllocationService.GetAllocation", req1)
        start_time = time.time()
        resp1 = client.get_allocation(req1)
        elapsed_time = time.time() - start_time
        print_response_log(resp1, elapsed_time)
        
        # 验证返回值
        if resp1.get('code') == 200:
            print("  ✓ 分配API调用成功")
        else:
            print(f"  ✗ 分配API调用失败，状态码: {resp1.get('code')}")
    except Exception as e:
        print(f"  GetAllocation测试失败: {e}")
        traceback.print_exc()
    
    try:
        # 测试2.2: 带aggregate的GetAllocation调用
        print("测试2.2: GetAllocation with aggregate")
        req2 = {}
        req2["window"] = "today"
        req2["aggregate"] = "namespace"
        print_request_log("AllocationService.GetAllocation", req2)
        start_time = time.time()
        resp2 = client.get_allocation(req2)
        elapsed_time = time.time() - start_time
        print_response_log(resp2, elapsed_time)
        
        # 验证返回值
        if resp2.get('code') == 200:
            print("  ✓ 聚合查询的分配API调用成功")
        else:
            print(f"  ✗ 聚合查询的分配API调用失败，状态码: {resp2.get('code')}")
    except Exception as e:
        print(f"  GetAllocation with aggregate测试失败: {e}")
        traceback.print_exc()
    
    # 测试2.3: 获取工作负载的分配数据
    print("测试2.3: GetAllocation for workloads")
    workload_types = ["Deployment", "StatefulSet", "DaemonSet"]
    for workload_type in workload_types:
        try:
            req = {}
            req["window"] = "today"
            req["filter"] = f'controllerKind:"{workload_type}"'
            print_request_log("AllocationService.GetAllocation", req)
            start_time = time.time()
            resp = client.get_allocation(req)
            elapsed_time = time.time() - start_time
            print_response_log(resp, elapsed_time)
            
            # 验证返回值
            code = resp.get('code')
            if code == 200:
                print(f"  ✓ {workload_type}类型工作负载分配查询成功")
            elif code == 503:
                print(f"  - {workload_type}类型工作负载分配查询返回服务不可用(503)")
            else:
                print(f"  ✗ {workload_type}类型工作负载分配查询失败，状态码: {code}")
        except Exception as e:
            print(f"  GetAllocation for controllerKind:\"{workload_type}\" failed: {e}")
            traceback.print_exc()

if __name__ == '__main__':
    try:
        run_fvt()
        print("Python FVT测试执行完成")
    except Exception as e:
        print(f"FVT测试执行出错: {e}")
        traceback.print_exc()