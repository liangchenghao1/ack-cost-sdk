package com.aliyun.container.service.cost.sdk.model;

import java.util.Map;

/**
 * 成本数据基类
 */
public class CostData {
    private String name = "";
    private Properties properties = new Properties();
    private String start = "";
    private String end = "";
    private double cpuCoreRequestAverage = 0.0;
    private double cpuCoreUsageAverage = 0.0;
    private double ramByteRequestAverage = 0.0;
    private double ramByteUsageAverage = 0.0;
    private double cost = 0.0;
    private double costRatio = 0.0;
    private double customCost = 0.0;

    /**
     * 获取名称
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     * @return CostData对象
     */
    public CostData setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 获取属性
     * @return 属性
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * 设置属性
     * @param properties 属性
     * @return CostData对象
     */
    public CostData setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * 获取开始时间
     * @return 开始时间
     */
    public String getStart() {
        return start;
    }

    /**
     * 设置开始时间
     * @param start 开始时间
     * @return CostData对象
     */
    public CostData setStart(String start) {
        this.start = start;
        return this;
    }

    /**
     * 获取结束时间
     * @return 结束时间
     */
    public String getEnd() {
        return end;
    }

    /**
     * 设置结束时间
     * @param end 结束时间
     * @return CostData对象
     */
    public CostData setEnd(String end) {
        this.end = end;
        return this;
    }

    /**
     * 获取请求的平均CPU核心数
     * @return 请求的平均CPU核心数
     */
    public double getCpuCoreRequestAverage() {
        return cpuCoreRequestAverage;
    }

    /**
     * 设置请求的平均CPU核心数
     * @param cpuCoreRequestAverage 请求的平均CPU核心数
     * @return CostData对象
     */
    public CostData setCpuCoreRequestAverage(double cpuCoreRequestAverage) {
        this.cpuCoreRequestAverage = cpuCoreRequestAverage;
        return this;
    }

    /**
     * 获取使用的平均CPU核心数
     * @return 使用的平均CPU核心数
     */
    public double getCpuCoreUsageAverage() {
        return cpuCoreUsageAverage;
    }

    /**
     * 设置使用的平均CPU核心数
     * @param cpuCoreUsageAverage 使用的平均CPU核心数
     * @return CostData对象
     */
    public CostData setCpuCoreUsageAverage(double cpuCoreUsageAverage) {
        this.cpuCoreUsageAverage = cpuCoreUsageAverage;
        return this;
    }

    /**
     * 获取请求的平均内存量
     * @return 请求的平均内存量
     */
    public double getRamByteRequestAverage() {
        return ramByteRequestAverage;
    }

    /**
     * 设置请求的平均内存量
     * @param ramByteRequestAverage 请求的平均内存量
     * @return CostData对象
     */
    public CostData setRamByteRequestAverage(double ramByteRequestAverage) {
        this.ramByteRequestAverage = ramByteRequestAverage;
        return this;
    }

    /**
     * 获取使用的平均内存量
     * @return 使用的平均内存量
     */
    public double getRamByteUsageAverage() {
        return ramByteUsageAverage;
    }

    /**
     * 设置使用的平均内存量
     * @param ramByteUsageAverage 使用的平均内存量
     * @return CostData对象
     */
    public CostData setRamByteUsageAverage(double ramByteUsageAverage) {
        this.ramByteUsageAverage = ramByteUsageAverage;
        return this;
    }

    /**
     * 获取估算成本
     * @return 估算成本
     */
    public double getCost() {
        return cost;
    }

    /**
     * 设置估算成本
     * @param cost 估算成本
     * @return CostData对象
     */
    public CostData setCost(double cost) {
        this.cost = cost;
        return this;
    }

    /**
     * 获取成本占比
     * @return 成本占比
     */
    public double getCostRatio() {
        return costRatio;
    }

    /**
     * 设置成本占比
     * @param costRatio 成本占比
     * @return CostData对象
     */
    public CostData setCostRatio(double costRatio) {
        this.costRatio = costRatio;
        return this;
    }

    /**
     * 获取自定义成本
     * @return 自定义成本
     */
    public double getCustomCost() {
        return customCost;
    }

    /**
     * 设置自定义成本
     * @param customCost 自定义成本
     * @return CostData对象
     */
    public CostData setCustomCost(double customCost) {
        this.customCost = customCost;
        return this;
    }
}