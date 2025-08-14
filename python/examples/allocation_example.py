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

        # 示例: 获取某个DaemonSet昨天分摊集群账单的费用
        print("=== 获取DaemonSet业务分摊账单 ===")
        example(client)

    except Exception as e:
        print(f"Error: {e}")


def example(client):
    try:
        # 获取某个DaemonSet昨天分摊集群账单的费用
        request = {
            'window': 'yesterday',
            'filter': 'namespace:"kube-system"+controllerKind:"DaemonSet"+label[app]:"terway-eniip"'
        }

        response = client.get_allocation(request)
        print(f"Response code: {response.get('code')}")
        
        if 'data' in response:
            print(f"Response data: {response.get('data')}")

    except Exception as e:
        print(f"Error in example: {e}")


if __name__ == "__main__":
    main()