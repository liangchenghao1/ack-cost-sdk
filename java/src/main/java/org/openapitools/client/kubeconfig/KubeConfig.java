package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Kubernetes kubeconfig structure
 */
public class KubeConfig {
    
    @JsonProperty("apiVersion")
    private String apiVersion;
    
    @JsonProperty("kind")
    private String kind;
    
    @JsonProperty("clusters")
    private List<KubeConfigCluster> clusters;
    
    @JsonProperty("users")
    private List<KubeConfigUser> users;
    
    @JsonProperty("contexts")
    private List<KubeConfigContext> contexts;
    
    @JsonProperty("current-context")
    private String currentContext;
    
    @JsonProperty("preferences")
    private Object preferences;
    
    // Constructors
    public KubeConfig() {}
    
    public KubeConfig(String apiVersion, String kind, List<KubeConfigCluster> clusters, 
                     List<KubeConfigUser> users, List<KubeConfigContext> contexts, String currentContext, Object preferences) {
        this.apiVersion = apiVersion;
        this.kind = kind;
        this.clusters = clusters;
        this.users = users;
        this.contexts = contexts;
        this.currentContext = currentContext;
        this.preferences = preferences;
    }
    
    // Getters and Setters
    public String getApiVersion() {
        return apiVersion;
    }
    
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
    
    public String getKind() {
        return kind;
    }
    
    public void setKind(String kind) {
        this.kind = kind;
    }
    
    public List<KubeConfigCluster> getClusters() {
        return clusters;
    }
    
    public void setClusters(List<KubeConfigCluster> clusters) {
        this.clusters = clusters;
    }
    
    public List<KubeConfigUser> getUsers() {
        return users;
    }
    
    public void setUsers(List<KubeConfigUser> users) {
        this.users = users;
    }
    
    public List<KubeConfigContext> getContexts() {
        return contexts;
    }
    
    public void setContexts(List<KubeConfigContext> contexts) {
        this.contexts = contexts;
    }
    
    public String getCurrentContext() {
        return currentContext;
    }
    
    public void setCurrentContext(String currentContext) {
        this.currentContext = currentContext;
    }
    
    public Object getPreferences() {
        return preferences;
    }
    
    public void setPreferences(Object preferences) {
        this.preferences = preferences;
    }
}
