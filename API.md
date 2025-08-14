# ACK Cost API 参考文档

## API 概述

阿里云ACK提供了三种成本数据查询API，用于不同场景的成本管理和分析：

### 1. Cost API（旧版）
- 用于查询Pod的实时估算成本和资源使用情况
- 支持按Namespace、Pod等维度查询
- 提供基本的成本指标，如CPU、内存使用情况和成本占比

### 2. Cost V2 API（推荐）
- 完全兼容Cost API，提供更多功能
- 支持更灵活的时间范围设置
- 提供多维度资源过滤（Namespace、Controller、Controller Kind、Pod、Label等）
- 支持结果分步(step参数)和聚合(aggregate参数)
- 可处理闲置成本、支持分摊和自定义资源成本权重

### 3. Allocation API
- 结合账单数据，提供业务分摊账单的费用
- 最小查询时间窗口为24小时
- 用于业务对账场景
- 响应结构与Cost V2 API一致

## API 详细说明

### Cost API

#### 请求地址
```
GET /api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/cost
```

#### 请求参数

| 参数 | 类型 | 必选 | 描述 |
|------|------|------|------|
| DimensionType | string | 否 | 成本统计维度，可选值：Namespace、Pod |
| Dimension | string | 否 | 成本筛选值 |
| LabelSelector | string | 否 | 通过应用标签筛选Pod |
| TimeUnit | string | 否 | 成本数据统计时间，可选值：hour、day、week、month |
| Summary | boolean | 否 | 是否返回筛选后的Pod数据总和 |

#### 响应参数

| 参数 | 类型 | 描述 |
|------|------|------|
| metadata | object | 成本元数据 |
| request | object | 请求资源 |
| usage | object | 使用资源 |
| limit | object | 限制资源 |
| perCorePricing | float | 单位CPU核的价格 |
| costRatio | float | 占整个集群成本比例 |
| cost | float | 成本 |
| customCost | float | 定价成本 |

### Cost V2 API

#### 请求地址
```
GET /api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/v2/cost
```

#### 请求参数

| 参数 | 类型 | 必选 | 描述 |
|------|------|------|------|
| window | string | 是 | 查询的持续时间 |
| filter | string | 否 | 资源过滤条件 |
| step | string | 否 | 时间分段 |
| aggregate | string | 否 | 聚合维度 |
| idle | boolean | 否 | 是否展示闲置成本 |
| shareIdle | boolean | 否 | 是否分摊闲置成本 |
| shareSplit | string | 否 | 闲置分摊策略 |
| idleByNode | boolean | 否 | 是否按节点维度聚合闲置成本 |
| format | string | 否 | 成本导出格式 |

#### 响应参数

| 参数 | 类型 | 描述 |
|------|------|------|
| properties | object | Kubernetes对象属性 |
| start | string | 成本集开始时间 |
| end | string | 成本集结束时间 |
| cpuCoreRequestAverage | float | 请求的平均CPU核心数 |
| cpuCoreUsageAverage | float | 使用的平均CPU核心数 |
| ramByteRequestAverage | float | 请求的平均内存量 |
| ramByteUsageAverage | float | 使用的平均内存量 |
| cost | float | 估算成本 |
| costRatio | float | 成本占比 |
| customCost | float | 自定义成本 |

### Allocation API

#### 请求地址
```
GET /api/v1/namespaces/kube-system/services/ack-metrics-adapter-api-service:8080/proxy/v2/allocation
```

#### 请求参数

| 参数 | 类型 | 必选 | 描述 |
|------|------|------|------|
| window | string | 是 | 查询的持续时间（最小24h） |
| filter | string | 否 | 资源过滤条件 |
| step | string | 否 | 时间分段 |
| aggregate | string | 否 | 聚合维度 |
| idle | boolean | 否 | 是否展示闲置成本 |
| shareIdle | boolean | 否 | 是否分摊闲置成本 |
| shareSplit | string | 否 | 闲置分摊策略 |
| idleByNode | boolean | 否 | 是否按节点维度聚合闲置成本 |
| targetType | string | 否 | 成本分摊的目标类型 |
| format | string | 否 | 成本导出格式 |

#### 响应参数

| 参数 | 类型 | 描述 |
|------|------|------|
| properties | object | Kubernetes对象属性 |
| start | string | 成本集开始时间 |
| end | string | 成本集结束时间 |
| cpuCoreRequestAverage | float | 请求的平均CPU核心数 |
| cpuCoreUsageAverage | float | 使用的平均CPU核心数 |
| ramByteRequestAverage | float | 请求的平均内存量 |
| ramByteUsageAverage | float | 使用的平均内存量 |
| cost | float | 分摊集群总账单的成本 |
| costRatio | float | 成本占比 |
| customCost | float | 自定义成本 |

## 错误码说明

| 错误码 | HTTP状态码 | 描述 |
|--------|------------|------|
| BadRequest | 400 | 请求参数错误 |
| Unauthorized | 401 | 未授权访问 |
| Forbidden | 403 | 禁止访问 |
| NotFound | 404 | 资源未找到 |
| TooManyRequests | 429 | 请求过于频繁 |
| InternalError | 500 | 服务器内部错误 |
| ServiceUnavailable | 503 | 服务不可用 |