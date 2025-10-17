package org.openapitools.client.kubeconfig;

/**
 * Authentication information extracted from kubeconfig
 */
public class KubeConfigAuth {
    
    private String serverUrl;
    private byte[] certData;
    private byte[] keyData;
    private byte[] caData;
    private boolean insecureSkip;
    
    // Constructors
    public KubeConfigAuth() {}
    
    public KubeConfigAuth(String serverUrl, byte[] certData, byte[] keyData, 
                         byte[] caData, boolean insecureSkip) {
        this.serverUrl = serverUrl;
        this.certData = certData;
        this.keyData = keyData;
        this.caData = caData;
        this.insecureSkip = insecureSkip;
    }
    
    // Getters and Setters
    public String getServerUrl() {
        return serverUrl;
    }
    
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    public byte[] getCertData() {
        return certData;
    }
    
    public void setCertData(byte[] certData) {
        this.certData = certData;
    }
    
    public byte[] getKeyData() {
        return keyData;
    }
    
    public void setKeyData(byte[] keyData) {
        this.keyData = keyData;
    }
    
    public byte[] getCaData() {
        return caData;
    }
    
    public void setCaData(byte[] caData) {
        this.caData = caData;
    }
    
    public boolean isInsecureSkip() {
        return insecureSkip;
    }
    
    public void setInsecureSkip(boolean insecureSkip) {
        this.insecureSkip = insecureSkip;
    }
}
