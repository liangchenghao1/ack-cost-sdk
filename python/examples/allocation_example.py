#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Allocation API 使用示例
"""

from ack_cost_sdk import Client, AllocationRequest


def example(client):
    """示例: 获取某个DaemonSet昨天分摊集群账单的费用"""
    print("=== 获取DaemonSet业务分摊账单 ===")
    
    request = AllocationRequest(
        window="yesterday",
        filter='namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
    )
    
    try:
        response = client.allocation.get_allocation(request)
        
        for data in response.data:
            for name, allocation in data.items():
                print(f"Name: {name}, Allocation Cost: {allocation.cost:.3f}, "
                      f"CPU Request: {allocation.cpu_core_request_average:.2f}, "
                      f"Memory Request: {allocation.ram_byte_request_average:.0f}")
    except Exception as e:
        print(f"Error: {e}")


def main():
    # 创建客户端
    client = Client(
        api_server="https://kubernetes.default.svc",
        retry_count=3,
        retry_wait=2
    )

    # 示例: 获取某个DaemonSet昨天分摊集群账单的费用
    example(client)


if __name__ == "__main__":
    main()