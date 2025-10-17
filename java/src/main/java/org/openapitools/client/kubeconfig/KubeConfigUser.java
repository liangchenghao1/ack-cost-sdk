package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kubernetes user from kubeconfig
 */
public class KubeConfigUser {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("user")
    private KubeConfigUserInfo user;
    
    // Constructors
    public KubeConfigUser() {}
    
    public KubeConfigUser(String name, KubeConfigUserInfo user) {
        this.name = name;
        this.user = user;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public KubeConfigUserInfo getUser() {
        return user;
    }
    
    public void setUser(KubeConfigUserInfo user) {
        this.user = user;
    }
}
