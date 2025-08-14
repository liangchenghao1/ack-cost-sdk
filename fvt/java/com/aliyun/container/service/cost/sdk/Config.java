package com.aliyun.container.service.cost.sdk;

public class Config {
    private String endpoint = "http://127.0.0.1:8080";
    private String accessKeyId;
    private String accessKeySecret;
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getAccessKeyId() {
        return accessKeyId;
    }
    
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }
    
    public String getAccessKeySecret() {
        return accessKeySecret;
    }
    
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }
}