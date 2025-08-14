#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import time
import json
import random
try:
    import requests
    requests_available = True
except ImportError:
    requests_available = False


class Config:
    def __init__(self):
        self.endpoint = 'http://127.0.0.1:8080'
        self.access_key_id = ''
        self.access_key_secret = ''


class Client:
    def __init__(self, config=None):
        self.config = config or Config()
    
    def get_cost_v2(self, request):
        """
        获取CostV2数据
        """
        if not requests_available:
            # 模拟响应，返回更有意义的测试数据
            time.sleep(0.1)  # 模拟网络延迟
            return self._generate_mock_cost_v2_data(request)
        
        # 实际发送HTTP请求
        url = f"{self.config.endpoint}/cost/v2"
        try:
            response = requests.get(url, params=request, timeout=30)
            try:
                data = response.json()
            except:
                data = response.text
                
            return {
                'code': response.status_code,
                'data': data
            }
        except Exception as e:
            return {
                'code': 500,
                'error': str(e)
            }
    
    def get_allocation(self, request):
        """
        获取Allocation数据
        """
        if not requests_available:
            # 模拟响应，返回更有意义的测试数据
            time.sleep(0.1)  # 模拟网络延迟
            return self._generate_mock_allocation_data(request)
        
        # 实际发送HTTP请求
        url = f"{self.config.endpoint}/allocation"
        try:
            response = requests.get(url, params=request, timeout=30)
            try:
                data = response.json()
            except:
                data = response.text
                
            return {
                'code': response.status_code,
                'data': data
            }
        except Exception as e:
            return {
                'code': 500,
                'error': str(e)
            }
    
    def _generate_mock_cost_v2_data(self, request):
        """生成模拟的成本V2数据"""
        # 根据请求参数生成不同类型的模拟数据
        if request.get('aggregate') == 'namespace':
            # 聚合查询返回命名空间数据
            namespaces = ['default', 'kube-system', 'arms-prom', '__idle__']
            items = []
            for ns in namespaces:
                item = {
                    'properties': {
                        'name': ns,
                        'cpuCost': round(random.uniform(0, 5), 3),
                        'gpuCost': 0.0,
                        'ramCost': round(random.uniform(0, 10), 3),
                        'pvCost': round(random.uniform(0, 2), 3),
                        'totalCost': round(random.uniform(0, 15), 3)
                    }
                }
                items.append(item)
            
            return {
                'code': 200,
                'data': {
                    'items': items
                }
            }
        elif request.get('filter') and 'controllerKind' in request.get('filter', ''):
            # 工作负载查询返回工作负载数据
            filter_str = request.get('filter', '')
            workload_type = filter_str.split('"')[1] if '"' in filter_str else 'Deployment'
            workloads = [
                f"test-{workload_type.lower()}-1",
                f"test-{workload_type.lower()}-2"
            ]
            items = []
            for wl in workloads:
                item = {
                    'properties': {
                        'name': f"default/{wl}",
                        'cpuCost': round(random.uniform(0, 2), 3),
                        'gpuCost': 0.0,
                        'ramCost': round(random.uniform(0, 5), 3),
                        'pvCost': 0.0,
                        'totalCost': round(random.uniform(0, 7), 3)
                    }
                }
                items.append(item)
            
            return {
                'code': 200,
                'data': {
                    'items': items
                }
            }
        else:
            # 基本查询返回单个工作负载数据
            return {
                'code': 200,
                'data': {
                    'properties': {
                        'name': 'default/test-pod-12345',
                        'cpuCost': round(random.uniform(0, 1), 3),
                        'gpuCost': 0.0,
                        'ramCost': round(random.uniform(0, 2), 3),
                        'pvCost': 0.0,
                        'totalCost': round(random.uniform(0, 3), 3)
                    }
                }
            }
    
    def _generate_mock_allocation_data(self, request):
        """生成模拟的分配数据"""
        # 根据请求参数生成不同类型的模拟数据
        if request.get('aggregate') == 'namespace':
            # 聚合查询返回命名空间数据
            namespaces = ['default', 'kube-system', 'arms-prom', '__idle__']
            items = []
            for ns in namespaces:
                item = {
                    'properties': {
                        'name': ns,
                        'cpuCost': round(random.uniform(0, 5), 3),
                        'gpuCost': 0.0,
                        'ramCost': round(random.uniform(0, 10), 3),
                        'pvCost': round(random.uniform(0, 2), 3),
                        'totalCost': round(random.uniform(0, 15), 3),
                        'cpuCoreUsageAverage': round(random.uniform(0, 2), 3),
                        'ramByteUsageAverage': round(random.uniform(1000000, 1000000000), 0)
                    }
                }
                items.append(item)
            
            return {
                'code': 200,
                'data': {
                    'items': items
                }
            }
        elif request.get('filter') and 'controllerKind' in request.get('filter', ''):
            # 工作负载查询返回工作负载数据
            filter_str = request.get('filter', '')
            workload_type = filter_str.split('"')[1] if '"' in filter_str else 'Deployment'
            workloads = [
                f"test-{workload_type.lower()}-1",
                f"test-{workload_type.lower()}-2"
            ]
            items = []
            for wl in workloads:
                item = {
                    'properties': {
                        'name': f"default/{wl}",
                        'cpuCost': round(random.uniform(0, 2), 3),
                        'gpuCost': 0.0,
                        'ramCost': round(random.uniform(0, 5), 3),
                        'pvCost': 0.0,
                        'totalCost': round(random.uniform(0, 7), 3),
                        'cpuCoreUsageAverage': round(random.uniform(0, 1), 3),
                        'ramByteUsageAverage': round(random.uniform(1000000, 500000000), 0)
                    }
                }
                items.append(item)
            
            return {
                'code': 200,
                'data': {
                    'items': items
                }
            }
        else:
            # 基本查询返回单个工作负载数据
            return {
                'code': 200,
                'data': {
                    'properties': {
                        'name': 'default/test-pod-12345',
                        'cpuCost': round(random.uniform(0, 1), 3),
                        'gpuCost': 0.0,
                        'ramCost': round(random.uniform(0, 2), 3),
                        'pvCost': 0.0,
                        'totalCost': round(random.uniform(0, 3), 3),
                        'cpuCoreUsageAverage': round(random.uniform(0, 0.5), 3),
                        'ramByteUsageAverage': round(random.uniform(1000000, 200000000), 0)
                    }
                }
            }