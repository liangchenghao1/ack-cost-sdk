package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kubernetes cluster information from kubeconfig
 */
public class KubeConfigClusterInfo {
    
    @JsonProperty("server")
    private String server;
    
    @JsonProperty("certificate-authority-data")
    private String certificateAuthorityData;
    
    @JsonProperty("certificate-authority")
    private String certificateAuthority;
    
    @JsonProperty("insecure-skip-tls-verify")
    private Boolean insecureSkipTlsVerify;
    
    // Constructors
    public KubeConfigClusterInfo() {}
    
    public KubeConfigClusterInfo(String server, String certificateAuthorityData, 
                                String certificateAuthority, Boolean insecureSkipTlsVerify) {
        this.server = server;
        this.certificateAuthorityData = certificateAuthorityData;
        this.certificateAuthority = certificateAuthority;
        this.insecureSkipTlsVerify = insecureSkipTlsVerify;
    }
    
    // Getters and Setters
    public String getServer() {
        return server;
    }
    
    public void setServer(String server) {
        this.server = server;
    }
    
    public String getCertificateAuthorityData() {
        return certificateAuthorityData;
    }
    
    public void setCertificateAuthorityData(String certificateAuthorityData) {
        this.certificateAuthorityData = certificateAuthorityData;
    }
    
    public String getCertificateAuthority() {
        return certificateAuthority;
    }
    
    public void setCertificateAuthority(String certificateAuthority) {
        this.certificateAuthority = certificateAuthority;
    }
    
    public Boolean getInsecureSkipTlsVerify() {
        return insecureSkipTlsVerify;
    }
    
    public void setInsecureSkipTlsVerify(Boolean insecureSkipTlsVerify) {
        this.insecureSkipTlsVerify = insecureSkipTlsVerify;
    }
}
