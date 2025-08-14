package com.aliyun.container.service.cost.sdk.model;

/**
 * Allocation API请求参数类
 */
public class AllocationRequest {
    private String window;
    private String filter;
    private String step;
    private String aggregate;
    private Boolean idle;
    private Boolean shareIdle;
    private String shareSplit;
    private Boolean idleByNode;
    private String targetType;
    private String format;

    /**
     * 获取查询的持续时间
     * @return 查询的持续时间
     */
    public String getWindow() {
        return window;
    }

    /**
     * 设置查询的持续时间
     * @param window 查询的持续时间
     * @return AllocationRequest对象
     */
    public AllocationRequest setWindow(String window) {
        this.window = window;
        return this;
    }

    /**
     * 获取资源过滤条件
     * @return 资源过滤条件
     */
    public String getFilter() {
        return filter;
    }

    /**
     * 设置资源过滤条件
     * @param filter 资源过滤条件
     * @return AllocationRequest对象
     */
    public AllocationRequest setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    /**
     * 获取时间分段
     * @return 时间分段
     */
    public String getStep() {
        return step;
    }

    /**
     * 设置时间分段
     * @param step 时间分段
     * @return AllocationRequest对象
     */
    public AllocationRequest setStep(String step) {
        this.step = step;
        return this;
    }

    /**
     * 获取聚合维度
     * @return 聚合维度
     */
    public String getAggregate() {
        return aggregate;
    }

    /**
     * 设置聚合维度
     * @param aggregate 聚合维度
     * @return AllocationRequest对象
     */
    public AllocationRequest setAggregate(String aggregate) {
        this.aggregate = aggregate;
        return this;
    }

    /**
     * 获取是否展示闲置成本
     * @return 是否展示闲置成本
     */
    public Boolean getIdle() {
        return idle;
    }

    /**
     * 设置是否展示闲置成本
     * @param idle 是否展示闲置成本
     * @return AllocationRequest对象
     */
    public AllocationRequest setIdle(Boolean idle) {
        this.idle = idle;
        return this;
    }

    /**
     * 获取是否分摊闲置成本
     * @return 是否分摊闲置成本
     */
    public Boolean getShareIdle() {
        return shareIdle;
    }

    /**
     * 设置是否分摊闲置成本
     * @param shareIdle 是否分摊闲置成本
     * @return AllocationRequest对象
     */
    public AllocationRequest setShareIdle(Boolean shareIdle) {
        this.shareIdle = shareIdle;
        return this;
    }

    /**
     * 获取闲置分摊策略
     * @return 闲置分摊策略
     */
    public String getShareSplit() {
        return shareSplit;
    }

    /**
     * 设置闲置分摊策略
     * @param shareSplit 闲置分摊策略
     * @return AllocationRequest对象
     */
    public AllocationRequest setShareSplit(String shareSplit) {
        this.shareSplit = shareSplit;
        return this;
    }

    /**
     * 获取是否按节点维度聚合闲置成本
     * @return 是否按节点维度聚合闲置成本
     */
    public Boolean getIdleByNode() {
        return idleByNode;
    }

    /**
     * 设置是否按节点维度聚合闲置成本
     * @param idleByNode 是否按节点维度聚合闲置成本
     * @return AllocationRequest对象
     */
    public AllocationRequest setIdleByNode(Boolean idleByNode) {
        this.idleByNode = idleByNode;
        return this;
    }

    /**
     * 获取成本分摊的目标类型
     * @return 成本分摊的目标类型
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * 设置成本分摊的目标类型
     * @param targetType 成本分摊的目标类型
     * @return AllocationRequest对象
     */
    public AllocationRequest setTargetType(String targetType) {
        this.targetType = targetType;
        return this;
    }

    /**
     * 获取成本导出格式
     * @return 成本导出格式
     */
    public String getFormat() {
        return format;
    }

    /**
     * 设置成本导出格式
     * @param format 成本导出格式
     * @return AllocationRequest对象
     */
    public AllocationRequest setFormat(String format) {
        this.format = format;
        return this;
    }
}