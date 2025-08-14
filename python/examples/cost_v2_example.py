#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import os

# 添加项目根目录到Python路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from ack_cost_sdk.client import Client, Config


def main():
    try:
        # 创建客户端
        config = Config()
        config.endpoint = 'http://127.0.0.1:8080'
        client = Client(config)

        # 示例1: 查询DaemonSet昨天的估算成本明细
        print("=== 示例1: 查询DaemonSet昨天的估算成本明细 ===")
        example1(client)

        # 示例2: 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
        print("\n=== 示例2: 获取某个Pod三小时内的估算成本明细 ===")
        example2(client)

        # 示例3: 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
        print("\n=== 示例3: 按Label聚合应用成本 ===")
        example3(client)

    except Exception as e:
        print(f"Error: {e}")


def example1(client):
    try:
        # 查询DaemonSet昨天的成本
        request = {
            'window': 'yesterday',
            'filter': 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
        }

        response = client.get_cost_v2(request)
        print(f"Response code: {response.get('code')}")
        
        if 'data' in response:
            print(f"Response data: {response.get('data')}")

    except Exception as e:
        print(f"Error in example1: {e}")


def example2(client):
    try:
        # 获取某个Pod三小时内的估算成本明细，成本集按小时维度拆分
        request = {
            'window': '2024-03-24T00:00:00Z,2024-03-24T03:00:00Z',
            'step': '1h',
            'filter': 'namespace:"kube-system"+pod:"terway-eniip-kz68n"'
        }

        response = client.get_cost_v2(request)
        print(f"Response code: {response.get('code')}")
        
        if 'data' in response:
            print(f"Response data: {response.get('data')}")

    except Exception as e:
        print(f"Error in example2: {e}")


def example3(client):
    try:
        # 获取所有Label Key为app的应用成本，成本集按Label Value进行聚合
        request = {
            'window': '2h',
            'aggregate': 'label:app',
            'idle': 'true'
        }

        response = client.get_cost_v2(request)
        print(f"Response code: {response.get('code')}")
        
        if 'data' in response:
            print(f"Response data: {response.get('data')}")

    except Exception as e:
        print(f"Error in example3: {e}")


if __name__ == "__main__":
    main()