package com.aliyun.container.service.cost.sdk;

/**
 * SDK客户端类
 */
public class Client {
    private final Config config;
    private final HttpUtil httpUtil;
    private final CostV2Service costV2Service;
    private final AllocationService allocationService;

    /**
     * 构造函数
     * @param config 客户端配置
     */
    public Client(Config config) {
        this.config = config;
        this.httpUtil = new HttpUtil(config);
        this.costV2Service = new CostV2Service(this);
        this.allocationService = new AllocationService(this);
    }

    /**
     * 获取客户端配置
     * @return 客户端配置
     */
    public Config getConfig() {
        return config;
    }

    /**
     * 获取HTTP工具
     * @return HTTP工具
     */
    public HttpUtil getHttpUtil() {
        return httpUtil;
    }

    /**
     * 获取Cost V2服务
     * @return Cost V2服务
     */
    public CostV2Service getCostV2() {
        return costV2Service;
    }

    /**
     * 获取Allocation服务
     * @return Allocation服务
     */
    public AllocationService getAllocation() {
        return allocationService;
    }
}