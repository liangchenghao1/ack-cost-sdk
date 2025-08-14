package com.aliyun.container.service.cost.sdk.model;

import java.util.Map;
import java.util.HashMap;

/**
 * Kubernetes对象属性类
 */
public class Properties {
    private String pod;
    private String node;
    private String namespace;
    private String controllerKind;
    private String controller;
    private String providerID;
    private Map<String, String> labels = new HashMap<>();

    /**
     * 获取Pod名称
     * @return Pod名称
     */
    public String getPod() {
        return pod;
    }

    /**
     * 设置Pod名称
     * @param pod Pod名称
     * @return Properties对象
     */
    public Properties setPod(String pod) {
        this.pod = pod;
        return this;
    }

    /**
     * 获取节点名称
     * @return 节点名称
     */
    public String getNode() {
        return node;
    }

    /**
     * 设置节点名称
     * @param node 节点名称
     * @return Properties对象
     */
    public Properties setNode(String node) {
        this.node = node;
        return this;
    }

    /**
     * 获取命名空间
     * @return 命名空间
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * 设置命名空间
     * @param namespace 命名空间
     * @return Properties对象
     */
    public Properties setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    /**
     * 获取控制器类型
     * @return 控制器类型
     */
    public String getControllerKind() {
        return controllerKind;
    }

    /**
     * 设置控制器类型
     * @param controllerKind 控制器类型
     * @return Properties对象
     */
    public Properties setControllerKind(String controllerKind) {
        this.controllerKind = controllerKind;
        return this;
    }

    /**
     * 获取控制器名称
     * @return 控制器名称
     */
    public String getController() {
        return controller;
    }

    /**
     * 设置控制器名称
     * @param controller 控制器名称
     * @return Properties对象
     */
    public Properties setController(String controller) {
        this.controller = controller;
        return this;
    }

    /**
     * 获取节点对应ECS实例ID
     * @return ECS实例ID
     */
    public String getProviderID() {
        return providerID;
    }

    /**
     * 设置节点对应ECS实例ID
     * @param providerID ECS实例ID
     * @return Properties对象
     */
    public Properties setProviderID(String providerID) {
        this.providerID = providerID;
        return this;
    }

    /**
     * 获取Pod标签
     * @return Pod标签
     */
    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * 设置Pod标签
     * @param labels Pod标签
     * @return Properties对象
     */
    public Properties setLabels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }
}