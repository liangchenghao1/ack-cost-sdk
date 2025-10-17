package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Utility class for working with kubeconfig files
 */
public class KubeConfigUtils {
    
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    /**
     * Load and parse a kubeconfig file
     * 
     * @param kubeconfigPath Path to kubeconfig file. If null, tries default locations
     * @return Parsed KubeConfig object
     * @throws IOException if kubeconfig cannot be loaded or parsed
     */
    public static KubeConfig loadKubeconfig(String kubeconfigPath) throws IOException {
        if (kubeconfigPath == null) {
            // Try default locations
            kubeconfigPath = getDefaultKubeconfigPath();
            if (kubeconfigPath == null) {
                throw new IOException("No kubeconfig file found in default locations");
            }
        }
        
        File kubeconfigFile = new File(kubeconfigPath);
        if (!kubeconfigFile.exists()) {
            throw new IOException("Kubeconfig file not found: " + kubeconfigPath);
        }
        
        try {
            return yamlMapper.readValue(new File(kubeconfigPath), KubeConfig.class);
        } catch (Exception e) {
            throw new IOException("Failed to parse kubeconfig: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract authentication information from kubeconfig
     * 
     * @param kubeconfigPath Path to kubeconfig file. If null, tries default locations
     * @return KubeConfigAuth object with authentication information
     * @throws IOException if authentication info cannot be extracted
     */
    public static KubeConfigAuth extractAuthFromKubeconfig(String kubeconfigPath) throws IOException {
        KubeConfig config = loadKubeconfig(kubeconfigPath);
        
        // Find current context
        KubeConfigContext currentContext = null;
        if (config.getContexts() != null) {
            for (KubeConfigContext ctx : config.getContexts()) {
                if (ctx.getName().equals(config.getCurrentContext())) {
                    currentContext = ctx;
                    break;
                }
            }
        }
        
        if (currentContext == null) {
            throw new IOException("Current context not found: " + config.getCurrentContext());
        }
        
        // Find cluster info
        KubeConfigCluster cluster = null;
        if (config.getClusters() != null) {
            for (KubeConfigCluster c : config.getClusters()) {
                if (c.getName().equals(currentContext.getContext().getCluster())) {
                    cluster = c;
                    break;
                }
            }
        }
        
        if (cluster == null) {
            throw new IOException("Cluster not found: " + currentContext.getContext().getCluster());
        }
        
        // Find user info
        KubeConfigUser user = null;
        if (config.getUsers() != null) {
            for (KubeConfigUser u : config.getUsers()) {
                if (u.getName().equals(currentContext.getContext().getUser())) {
                    user = u;
                    break;
                }
            }
        }
        
        if (user == null) {
            throw new IOException("User not found: " + currentContext.getContext().getUser());
        }
        
        KubeConfigAuth auth = new KubeConfigAuth();
        auth.setServerUrl(cluster.getCluster().getServer());
        auth.setInsecureSkip(cluster.getCluster().getInsecureSkipTlsVerify() != null && 
                           cluster.getCluster().getInsecureSkipTlsVerify());
        
        // Extract CA data
        if (cluster.getCluster().getCertificateAuthorityData() != null) {
            try {
                auth.setCaData(Base64.getDecoder().decode(cluster.getCluster().getCertificateAuthorityData()));
            } catch (Exception e) {
                throw new IOException("Failed to decode CA data: " + e.getMessage(), e);
            }
        } else if (cluster.getCluster().getCertificateAuthority() != null) {
            try {
                auth.setCaData(Files.readAllBytes(Paths.get(cluster.getCluster().getCertificateAuthority())));
            } catch (Exception e) {
                throw new IOException("Failed to read CA file: " + e.getMessage(), e);
            }
        }
        
        // Extract client certificate and key
        if (user.getUser().getClientCertificateData() != null) {
            try {
                auth.setCertData(Base64.getDecoder().decode(user.getUser().getClientCertificateData()));
            } catch (Exception e) {
                throw new IOException("Failed to decode client certificate data: " + e.getMessage(), e);
            }
        } else if (user.getUser().getClientCertificate() != null) {
            try {
                auth.setCertData(Files.readAllBytes(Paths.get(user.getUser().getClientCertificate())));
            } catch (Exception e) {
                throw new IOException("Failed to read client certificate file: " + e.getMessage(), e);
            }
        }
        
        if (user.getUser().getClientKeyData() != null) {
            try {
                auth.setKeyData(Base64.getDecoder().decode(user.getUser().getClientKeyData()));
            } catch (Exception e) {
                throw new IOException("Failed to decode client key data: " + e.getMessage(), e);
            }
        } else if (user.getUser().getClientKey() != null) {
            try {
                auth.setKeyData(Files.readAllBytes(Paths.get(user.getUser().getClientKey())));
            } catch (Exception e) {
                throw new IOException("Failed to read client key file: " + e.getMessage(), e);
            }
        }
        
        return auth;
    }
    
    /**
     * Get default kubeconfig file path
     * 
     * @return Path to default kubeconfig file, or null if not found
     */
    private static String getDefaultKubeconfigPath() {
        // Check KUBECONFIG environment variable
        String kubeconfigEnv = System.getenv("KUBECONFIG");
        if (kubeconfigEnv != null && new File(kubeconfigEnv).exists()) {
            return kubeconfigEnv;
        }
        
        // Check default location
        String homeDir = System.getProperty("user.home");
        String defaultPath = homeDir + File.separator + ".kube" + File.separator + "config";
        if (new File(defaultPath).exists()) {
            return defaultPath;
        }
        
        return null;
    }
}
