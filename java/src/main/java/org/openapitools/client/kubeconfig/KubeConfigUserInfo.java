package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kubernetes user information from kubeconfig
 */
public class KubeConfigUserInfo {
    
    @JsonProperty("client-certificate-data")
    private String clientCertificateData;
    
    @JsonProperty("client-key-data")
    private String clientKeyData;
    
    @JsonProperty("client-certificate")
    private String clientCertificate;
    
    @JsonProperty("client-key")
    private String clientKey;
    
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("password")
    private String password;
    
    // Constructors
    public KubeConfigUserInfo() {}
    
    public KubeConfigUserInfo(String clientCertificateData, String clientKeyData, 
                             String clientCertificate, String clientKey, String token, 
                             String username, String password) {
        this.clientCertificateData = clientCertificateData;
        this.clientKeyData = clientKeyData;
        this.clientCertificate = clientCertificate;
        this.clientKey = clientKey;
        this.token = token;
        this.username = username;
        this.password = password;
    }
    
    // Getters and Setters
    public String getClientCertificateData() {
        return clientCertificateData;
    }
    
    public void setClientCertificateData(String clientCertificateData) {
        this.clientCertificateData = clientCertificateData;
    }
    
    public String getClientKeyData() {
        return clientKeyData;
    }
    
    public void setClientKeyData(String clientKeyData) {
        this.clientKeyData = clientKeyData;
    }
    
    public String getClientCertificate() {
        return clientCertificate;
    }
    
    public void setClientCertificate(String clientCertificate) {
        this.clientCertificate = clientCertificate;
    }
    
    public String getClientKey() {
        return clientKey;
    }
    
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
