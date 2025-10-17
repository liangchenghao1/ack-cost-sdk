/**
 * Kubernetes kubeconfig support for client authentication
 */

const fs = require('fs');
const path = require('path');
const yaml = require('js-yaml');

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
    }
}

/**
 * Load and parse a kubeconfig file
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

    try {
        const fileContents = fs.readFileSync(kubeconfigPath, 'utf8');
        return yaml.load(fileContents);
    } catch (error) {
        throw new Error(`Failed to parse kubeconfig: ${error.message}`);
    }
}

/**
 * Extract authentication information from kubeconfig
 * @param {string} kubeconfigPath - Path to kubeconfig file. If null, tries default locations
 * @returns {KubeConfigAuth} Authentication information
 * @throws {Error} If kubeconfig cannot be loaded or parsed, or auth info cannot be extracted
 */
function extractAuthFromKubeconfig(kubeconfigPath = null) {
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
    const caData = decodeOrReadFile(cluster.cluster['certificate-authority-data'], cluster.cluster['certificate-authority']);
    const certData = decodeOrReadFile(user.user['client-certificate-data'], user.user['client-certificate']);
    const keyData = decodeOrReadFile(user.user['client-key-data'], user.user['client-key']);
    const insecureSkip = cluster.cluster['insecure-skip-tls-verify'] || false;

    return new KubeConfigAuth(serverUrl, certData, keyData, caData, insecureSkip);
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
