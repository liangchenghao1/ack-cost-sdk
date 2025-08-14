package com.aliyun.container.service.cost.sdk.model;

import java.util.List;
import java.util.Map;

/**
 * Cost V2 API响应类
 */
public class CostV2Response {
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
     * @return CostV2Response对象
     */
    public CostV2Response setData(List<Map<String, CostData>> data) {
        this.data = data;
        return this;
    }
}