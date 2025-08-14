package com.aliyun.container.service.cost.sdk.model;

import java.util.List;
import java.util.Map;

/**
 * Allocation API响应类
 */
public class AllocationResponse {
    private List<Map<String, CostData>> data;

    /**
     * 获取数据数组
     * @return 数据数组
     */
    public List<Map<String, CostData>> getData() {
        return data;
    }

    /**
     * 设置数据数组
     * @param data 数据数组
     * @return AllocationResponse对象
     */
    public AllocationResponse setData(List<Map<String, CostData>> data) {
        this.data = data;
        return this;
    }
}