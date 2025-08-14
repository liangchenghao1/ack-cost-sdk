#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Cost V2 API 使用示例
"""

import sys
from ack_cost_sdk import Client, CostV2Request


def example1(client):
    """示例1: 查询DaemonSet昨天的估算成本明细"""
    print("=== 示例1: 查询DaemonSet昨天的估算成本明细 ===")
    
    request = CostV2Request(
        window="yesterday",
        filter='namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
    )
    
    try:
        response = client.cost_v2.get_cost_v2(request)
        
        for data in response.data:
            for name, cost in data.items():
                print(f"Name: {name}, Cost: {cost.cost:.3f}, CPU Request: {cost.cpu_core_request_average:.2f}, "
                      f"Memory Request: {cost.ram_byte_request_average:.0f}")
    except Exception as e:
        print(f"Error: {e}")


def example2(client):
    """示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分"""
    print("\n=== 示例2: 获取某个Pod三小时内的估算成本明细 ===")
    
    request = CostV2Request(
        window="2024-03-24T00:00:00Z,2024-03-24T03:00:00Z",
        step="1h",
        filter='namespace:"kube-system"+pod:"terway-eniip-kz68n"'
    )
    
    try:
        response = client.cost_v2.get_cost_v2(request)
        
        for data in response.data:
            for name, cost in data.items():
                print(f"Name: {name}, Period: {cost.start} - {cost.end}, Cost: {cost.cost:.3f}")
    except Exception as e:
        print(f"Error: {e}")


def example3(client):
    """示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合"""
    print("\n=== 示例3: 按Label聚合应用成本 ===")
    
    request = CostV2Request(
        window="2h",
        aggregate="label:app",
        idle=True
    )
    
    try:
        response = client.cost_v2.get_cost_v2(request)
        
        for data in response.data:
            for name, cost in data.items():
                print(f"App: {name}, Cost: {cost.cost:.3f}, CPU Usage: {cost.cpu_core_usage_average:.3f}, "
                      f"Memory Usage: {cost.ram_byte_usage_average:.0f}")
    except Exception as e:
        print(f"Error: {e}")


def main():
    # 创建客户端
    client = Client(
        api_server="https://kubernetes.default.svc",
        retry_count=3,
        retry_wait=2
    )

    # 示例1: 查询DaemonSet昨天的估算成本明细
    example1(client)

    # 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
    example2(client)

    # 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
    example3(client)


if __name__ == "__main__":
    main()