#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
import os
import unittest
from unittest.mock import patch

# 添加项目根目录到Python路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from ack_cost_sdk.client import Client, Config


class TestConfig(unittest.TestCase):
    def setUp(self):
        self.config = Config()

    def test_config_creation(self):
        """测试Config类的创建"""
        self.assertIsNotNone(self.config)
        self.assertEqual(self.config.endpoint, 'http://127.0.0.1:8080')
        self.assertEqual(self.config.access_key_id, '')
        self.assertEqual(self.config.access_key_secret, '')

    def test_config_endpoint_setter(self):
        """测试Config的endpoint设置"""
        test_endpoint = 'http://test.endpoint.com'
        self.config.endpoint = test_endpoint
        self.assertEqual(self.config.endpoint, test_endpoint)


class TestClient(unittest.TestCase):
    def setUp(self):
        self.config = Config()
        self.config.endpoint = 'http://127.0.0.1:8080'
        self.client = Client(self.config)

    def test_client_creation(self):
        """测试Client类的创建"""
        self.assertIsNotNone(self.client)
        self.assertIsNotNone(self.client.config)
        self.assertEqual(self.client.config.endpoint, 'http://127.0.0.1:8080')

    def test_client_creation_without_config(self):
        """测试不带配置的Client创建"""
        client = Client()
        self.assertIsNotNone(client)
        self.assertIsNotNone(client.config)
        self.assertEqual(client.config.endpoint, 'http://127.0.0.1:8080')

    @patch('ack_cost_sdk.client.requests_available', False)
    def test_get_cost_v2_with_mock_data(self):
        """测试get_cost_v2返回模拟数据"""
        request = {'window': 'today'}
        response = self.client.get_cost_v2(request)
        
        self.assertIsNotNone(response)
        self.assertIn('code', response)
        self.assertEqual(response['code'], 200)
        self.assertIn('data', response)

    @patch('ack_cost_sdk.client.requests_available', False)
    def test_get_allocation_with_mock_data(self):
        """测试get_allocation返回模拟数据"""
        request = {'window': 'today'}
        response = self.client.get_allocation(request)
        
        self.assertIsNotNone(response)
        self.assertIn('code', response)
        self.assertEqual(response['code'], 200)
        self.assertIn('data', response)

    @patch('ack_cost_sdk.client.requests_available', False)
    def test_get_cost_v2_with_namespace_aggregation(self):
        """测试get_cost_v2命名空间聚合数据"""
        request = {
            'window': 'today',
            'aggregate': 'namespace'
        }
        response = self.client.get_cost_v2(request)
        
        self.assertIsNotNone(response)
        self.assertEqual(response['code'], 200)
        self.assertIn('data', response)
        self.assertIn('items', response['data'])

    @patch('ack_cost_sdk.client.requests_available', False)
    def test_get_allocation_with_namespace_aggregation(self):
        """测试get_allocation命名空间聚合数据"""
        request = {
            'window': 'today',
            'aggregate': 'namespace'
        }
        response = self.client.get_allocation(request)
        
        self.assertIsNotNone(response)
        self.assertEqual(response['code'], 200)
        self.assertIn('data', response)
        self.assertIn('items', response['data'])


if __name__ == '__main__':
    unittest.main()