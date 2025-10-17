package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kubernetes cluster from kubeconfig
 */
public class KubeConfigCluster {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("cluster")
    private KubeConfigClusterInfo cluster;
    
    // Constructors
    public KubeConfigCluster() {}
    
    public KubeConfigCluster(String name, KubeConfigClusterInfo cluster) {
        this.name = name;
        this.cluster = cluster;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public KubeConfigClusterInfo getCluster() {
        return cluster;
    }
    
    public void setCluster(KubeConfigClusterInfo cluster) {
        this.cluster = cluster;
    }
}
