package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kubernetes context information from kubeconfig
 */
public class KubeConfigContextInfo {
    
    @JsonProperty("cluster")
    private String cluster;
    
    @JsonProperty("user")
    private String user;
    
    // Constructors
    public KubeConfigContextInfo() {}
    
    public KubeConfigContextInfo(String cluster, String user) {
        this.cluster = cluster;
        this.user = user;
    }
    
    // Getters and Setters
    public String getCluster() {
        return cluster;
    }
    
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
    
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
}
