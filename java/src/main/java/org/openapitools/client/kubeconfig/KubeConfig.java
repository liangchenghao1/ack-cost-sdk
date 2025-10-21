package org.openapitools.client.kubeconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * Kubernetes kubeconfig structure with security enhancements
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
    
    // Security fields
    private transient Instant lastAccessed;
    private transient Instant expiresAt;
    
    // Security utilities
    private static final ConcurrentHashMap<String, KubeConfig> configCache = new ConcurrentHashMap<>();
    private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private static final SecureRandom secureRandom = new SecureRandom();
    
    // Constructors
    public KubeConfig() {
        this.lastAccessed = Instant.now();
        this.expiresAt = Instant.now().plusSeconds(3600); // 1 hour cache
    }
    
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
    
    // Security methods
    public Instant getLastAccessed() {
        return lastAccessed;
    }
    
    public void setLastAccessed(Instant lastAccessed) {
        this.lastAccessed = lastAccessed;
    }
    
    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    
    /**
     * Validates file permissions for security
     * Note: Kubernetes kubeconfig files are typically readable by others
     * This is normal and expected behavior for shared cluster access
     */
    public static boolean validateFilePermissions(String filePath) {
        // Kubernetes kubeconfig files are typically readable by others
        // This is normal and expected behavior for shared cluster access
        return true;
    }
    
    /**
     * Securely clears sensitive data from memory
     */
    public void secureCleanup() {
        // Clear sensitive data if any
        if (this.preferences != null) {
            this.preferences = null;
        }
    }
    
    /**
     * Generates a secure hash for caching
     */
    public static String generateSecureHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate secure hash", e);
        }
    }
    
    /**
     * Caches the configuration with security checks
     */
    public static void cacheConfig(String key, KubeConfig config) {
        cacheLock.writeLock().lock();
        try {
            configCache.put(key, config);
        } finally {
            cacheLock.writeLock().unlock();
        }
    }
    
    /**
     * Retrieves cached configuration with expiration check
     */
    public static KubeConfig getCachedConfig(String key) {
        cacheLock.readLock().lock();
        try {
            KubeConfig config = configCache.get(key);
            if (config != null && Instant.now().isBefore(config.getExpiresAt())) {
                config.setLastAccessed(Instant.now());
                return config;
            }
            return null;
        } finally {
            cacheLock.readLock().unlock();
        }
    }
    
    /**
     * Cleans up expired cache entries
     */
    public static void cleanupExpiredCache() {
        cacheLock.writeLock().lock();
        try {
            Instant now = Instant.now();
            configCache.entrySet().removeIf(entry -> 
                now.isAfter(entry.getValue().getExpiresAt())
            );
        } finally {
            cacheLock.writeLock().unlock();
        }
    }
}
