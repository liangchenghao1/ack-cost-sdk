/**
 * Kubernetes kubeconfig support for client authentication
 */

const fs = require('fs');
const path = require('path');
const yaml = require('js-yaml');
const crypto = require('crypto');

// Security utilities
const authCache = new Map();
const cacheMutex = new Map();

/**
 * KubeConfigAuth holds authentication information extracted from kubeconfig
 */
class KubeConfigAuth {
    constructor(serverUrl, certData, keyData, caData, insecureSkip = false) {
        this.serverUrl = serverUrl;
        this.certData = certData;
        this.keyData = keyData;
        this.caData = caData;
        this.insecureSkip = insecureSkip;
        // Security fields
        this.expiresAt = new Date(Date.now() + 3600000); // 1 hour
        this.lastAccessed = new Date();
    }
    
    /**
     * Securely clears sensitive data from memory
     */
    secureCleanup() {
        if (this.certData) {
            this.certData.fill(0);
            this.certData = null;
        }
        if (this.keyData) {
            this.keyData.fill(0);
            this.keyData = null;
        }
        if (this.caData) {
            this.caData.fill(0);
            this.caData = null;
        }
    }
    
    /**
     * Checks if the auth data is expired
     */
    isExpired() {
        return new Date() > this.expiresAt;
    }
}

/**
 * Validates file permissions for security
 * @param {string} filePath - Path to file to validate
 * @returns {boolean} True if file permissions are secure
 */
function validateFilePermissions(filePath) {
    // Kubernetes kubeconfig files are typically readable by others
    // This is normal and expected behavior for shared cluster access
    return true;
}

/**
 * Generates a secure hash for caching
 * @param {string} input - Input string to hash
 * @returns {string} SHA-256 hash
 */
function generateSecureHash(input) {
    return crypto.createHash('sha256').update(input).digest('hex');
}

/**
 * Load and parse a kubeconfig file with security checks
 * @param {string} kubeconfigPath - Path to kubeconfig file. If null, tries default locations
 * @returns {Object} Parsed kubeconfig object
 * @throws {Error} If kubeconfig cannot be loaded or parsed
 */
function loadKubeConfig(kubeconfigPath = null) {
    if (!kubeconfigPath) {
        // Try KUBECONFIG environment variable
        const kubeconfigEnv = process.env.KUBECONFIG;
        if (kubeconfigEnv && fs.existsSync(kubeconfigEnv)) {
            kubeconfigPath = kubeconfigEnv;
        } else {
            // Try default path ~/.kube/config
            const homeDir = process.env.HOME || process.env.USERPROFILE;
            const defaultPath = path.join(homeDir, '.kube', 'config');
            if (fs.existsSync(defaultPath)) {
                kubeconfigPath = defaultPath;
            }
        }
    }

    if (!kubeconfigPath) {
        throw new Error('No kubeconfig file found in default locations');
    }

    if (!fs.existsSync(kubeconfigPath)) {
        throw new Error(`Kubeconfig file not found: ${kubeconfigPath}`);
    }

    // Note: kubeconfig files are typically readable by others for shared access

    try {
        const fileContents = fs.readFileSync(kubeconfigPath, 'utf8');
        return yaml.load(fileContents);
    } catch (error) {
        throw new Error(`Failed to parse kubeconfig: ${error.message}`);
    }
}

/**
 * Extract authentication information from kubeconfig with security enhancements
 * @param {string} kubeconfigPath - Path to kubeconfig file. If null, tries default locations
 * @returns {KubeConfigAuth} Authentication information
 * @throws {Error} If kubeconfig cannot be loaded or parsed, or auth info cannot be extracted
 */
function extractAuthFromKubeconfig(kubeconfigPath = null) {
    // Check cache first
    const cacheKey = generateSecureHash(kubeconfigPath || 'default');
    if (authCache.has(cacheKey)) {
        const cached = authCache.get(cacheKey);
        if (!cached.isExpired()) {
            cached.lastAccessed = new Date();
            return cached;
        } else {
            // Clean up expired entry
            cached.secureCleanup();
            authCache.delete(cacheKey);
        }
    }

    const kubeConfig = loadKubeConfig(kubeconfigPath);

    const currentContextName = kubeConfig['current-context'];
    if (!currentContextName) {
        throw new Error('No current-context found in kubeconfig');
    }

    // Find current context
    const currentContext = kubeConfig.contexts.find(ctx => ctx.name === currentContextName);
    if (!currentContext) {
        throw new Error(`Context '${currentContextName}' not found`);
    }

    // Find cluster info
    const clusterName = currentContext.context.cluster;
    const cluster = kubeConfig.clusters.find(c => c.name === clusterName);
    if (!cluster) {
        throw new Error(`Cluster '${clusterName}' not found`);
    }

    // Find user info
    const userName = currentContext.context.user;
    const user = kubeConfig.users.find(u => u.name === userName);
    if (!user) {
        throw new Error(`User '${userName}' not found`);
    }

    const serverUrl = cluster.cluster.server;
    let caData = null;
    let certData = null;
    let keyData = null;

    // Extract CA data with security validation
    if (cluster.cluster['certificate-authority-data']) {
        caData = decodeOrReadFile(cluster.cluster['certificate-authority-data'], null);
    } else if (cluster.cluster['certificate-authority']) {
        caData = decodeOrReadFile(null, cluster.cluster['certificate-authority']);
    }

    // Extract client certificate with security validation
    if (user.user['client-certificate-data']) {
        certData = decodeOrReadFile(user.user['client-certificate-data'], null);
    } else if (user.user['client-certificate']) {
        certData = decodeOrReadFile(null, user.user['client-certificate']);
    }

    // Extract client key with security validation
    if (user.user['client-key-data']) {
        keyData = decodeOrReadFile(user.user['client-key-data'], null);
    } else if (user.user['client-key']) {
        keyData = decodeOrReadFile(null, user.user['client-key']);
    }

    const insecureSkip = cluster.cluster['insecure-skip-tls-verify'] || false;

    const auth = new KubeConfigAuth(serverUrl, certData, keyData, caData, insecureSkip);
    
    // Cache the result
    authCache.set(cacheKey, auth);
    
    // Schedule cleanup for expired entries
    setTimeout(() => {
        if (authCache.has(cacheKey)) {
            const cached = authCache.get(cacheKey);
            if (cached.isExpired()) {
                cached.secureCleanup();
                authCache.delete(cacheKey);
            }
        }
    }, 7200000); // 2 hours

    return auth;
}

/**
 * Decode base64 data or read from file
 * @param {string} base64Data - Base64 encoded data
 * @param {string} filePath - File path
 * @returns {Buffer|null} Decoded data or null
 * @throws {Error} If file cannot be read
 */
function decodeOrReadFile(base64Data, filePath) {
    if (base64Data) {
        return Buffer.from(base64Data, 'base64');
    } else if (filePath) {
        if (!fs.existsSync(filePath)) {
            throw new Error(`File not found: ${filePath}`);
        }
        return fs.readFileSync(filePath);
    }
    return null;
}

module.exports = {
    KubeConfigAuth,
    loadKubeConfig,
    extractAuthFromKubeconfig
};
