package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kubernetes context from kubeconfig
 */
public class KubeConfigContext {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("context")
    private KubeConfigContextInfo context;
    
    // Constructors
    public KubeConfigContext() {}
    
    public KubeConfigContext(String name, KubeConfigContextInfo context) {
        this.name = name;
        this.context = context;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public KubeConfigContextInfo getContext() {
        return context;
    }
    
    public void setContext(KubeConfigContextInfo context) {
        this.context = context;
    }
}
