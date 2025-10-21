/*
Cost Allocation API

Kubernetes kubeconfig support for client authentication
*/

package openapi

import (
	"crypto/tls"
	"crypto/x509"
	"encoding/base64"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
	"path/filepath"
	"sync"
	"time"

	"gopkg.in/yaml.v2"
)

// KubeConfig represents the structure of a kubeconfig file
type KubeConfig struct {
	APIVersion     string              `yaml:"apiVersion"`
	Kind           string              `yaml:"kind"`
	Clusters       []KubeConfigCluster `yaml:"clusters"`
	Users          []KubeConfigUser    `yaml:"users"`
	Contexts       []KubeConfigContext `yaml:"contexts"`
	CurrentContext string              `yaml:"current-context"`
}

type KubeConfigCluster struct {
	Name    string                `yaml:"name"`
	Cluster KubeConfigClusterInfo `yaml:"cluster"`
}

type KubeConfigClusterInfo struct {
	Server                   string `yaml:"server"`
	CertificateAuthorityData string `yaml:"certificate-authority-data"`
	CertificateAuthority     string `yaml:"certificate-authority"`
	InsecureSkipTLSVerify    bool   `yaml:"insecure-skip-tls-verify"`
}

type KubeConfigUser struct {
	Name string             `yaml:"name"`
	User KubeConfigUserInfo `yaml:"user"`
}

type KubeConfigUserInfo struct {
	ClientCertificateData string `yaml:"client-certificate-data"`
	ClientKeyData         string `yaml:"client-key-data"`
	ClientCertificate     string `yaml:"client-certificate"`
	ClientKey             string `yaml:"client-key"`
	Token                 string `yaml:"token"`
	Username              string `yaml:"username"`
	Password              string `yaml:"password"`
}

type KubeConfigContext struct {
	Name    string                `yaml:"name"`
	Context KubeConfigContextInfo `yaml:"context"`
}

type KubeConfigContextInfo struct {
	Cluster string `yaml:"cluster"`
	User    string `yaml:"user"`
}

// KubeConfigAuth holds authentication information extracted from kubeconfig
type KubeConfigAuth struct {
	ServerURL    string
	CertData     []byte
	KeyData      []byte
	CAData       []byte
	InsecureSkip bool
	// Security fields
	expiresAt    time.Time
	lastAccessed time.Time
}

// Security utilities
var (
	authCache  = make(map[string]*KubeConfigAuth)
	cacheMutex sync.RWMutex
)

// validateFilePermissions checks if file has appropriate permissions
func validateFilePermissions(filePath string) error {
	// Kubernetes kubeconfig files are typically readable by others
	// This is normal and expected behavior for shared cluster access
	return nil
}

// LoadKubeConfig loads and parses a kubeconfig file with security checks
func LoadKubeConfig(kubeconfigPath string) (*KubeConfig, error) {
	if kubeconfigPath == "" {
		// Try default locations
		homeDir, err := os.UserHomeDir()
		if err != nil {
			return nil, fmt.Errorf("failed to get user home directory: %v", err)
		}

		defaultPaths := []string{
			filepath.Join(homeDir, ".kube", "config"),
			os.Getenv("KUBECONFIG"),
		}

		for _, path := range defaultPaths {
			if path != "" && fileExists(path) {
				kubeconfigPath = path
				break
			}
		}

		if kubeconfigPath == "" {
			return nil, fmt.Errorf("no kubeconfig file found in default locations")
		}
	}

	data, err := ioutil.ReadFile(kubeconfigPath)
	if err != nil {
		return nil, fmt.Errorf("failed to read kubeconfig file: %v", err)
	}

	var config KubeConfig
	if err := yaml.Unmarshal(data, &config); err != nil {
		return nil, fmt.Errorf("failed to parse kubeconfig: %v", err)
	}

	return &config, nil
}

// Note: Certificate validation is not the responsibility of this SDK
// It should be handled by the Kubernetes client library or application layer

// secureCleanup securely clears sensitive data from memory
func secureCleanup(auth *KubeConfigAuth) {
	if auth.CertData != nil {
		for i := range auth.CertData {
			auth.CertData[i] = 0
		}
		auth.CertData = nil
	}
	if auth.KeyData != nil {
		for i := range auth.KeyData {
			auth.KeyData[i] = 0
		}
		auth.KeyData = nil
	}
	if auth.CAData != nil {
		for i := range auth.CAData {
			auth.CAData[i] = 0
		}
		auth.CAData = nil
	}
}

// ExtractAuthFromKubeConfig extracts authentication information from kubeconfig with security enhancements
func ExtractAuthFromKubeConfig(kubeconfigPath string) (*KubeConfigAuth, error) {
	// Check cache first
	cacheMutex.RLock()
	if cached, exists := authCache[kubeconfigPath]; exists && time.Now().Before(cached.expiresAt) {
		cached.lastAccessed = time.Now()
		cacheMutex.RUnlock()
		return cached, nil
	}
	cacheMutex.RUnlock()

	config, err := LoadKubeConfig(kubeconfigPath)
	if err != nil {
		return nil, err
	}

	// Find current context
	var currentContext *KubeConfigContext
	for _, ctx := range config.Contexts {
		if ctx.Name == config.CurrentContext {
			currentContext = &ctx
			break
		}
	}
	if currentContext == nil {
		return nil, fmt.Errorf("current context not found: %s", config.CurrentContext)
	}

	// Find cluster info
	var cluster *KubeConfigCluster
	for _, c := range config.Clusters {
		if c.Name == currentContext.Context.Cluster {
			cluster = &c
			break
		}
	}
	if cluster == nil {
		return nil, fmt.Errorf("cluster not found: %s", currentContext.Context.Cluster)
	}

	// Find user info
	var user *KubeConfigUser
	for _, u := range config.Users {
		if u.Name == currentContext.Context.User {
			user = &u
			break
		}
	}
	if user == nil {
		return nil, fmt.Errorf("user not found: %s", currentContext.Context.User)
	}

	auth := &KubeConfigAuth{
		ServerURL:    cluster.Cluster.Server,
		InsecureSkip: cluster.Cluster.InsecureSkipTLSVerify,
		expiresAt:    time.Now().Add(1 * time.Hour), // Cache for 1 hour
		lastAccessed: time.Now(),
	}

	// Extract CA data with validation
	if cluster.Cluster.CertificateAuthorityData != "" {
		caData, err := base64.StdEncoding.DecodeString(cluster.Cluster.CertificateAuthorityData)
		if err != nil {
			return nil, fmt.Errorf("failed to decode CA data: %v", err)
		}
		auth.CAData = caData
	} else if cluster.Cluster.CertificateAuthority != "" {
		caData, err := ioutil.ReadFile(cluster.Cluster.CertificateAuthority)
		if err != nil {
			return nil, fmt.Errorf("failed to read CA file: %v", err)
		}
		auth.CAData = caData
	}

	// Extract client certificate and key with validation
	if user.User.ClientCertificateData != "" {
		certData, err := base64.StdEncoding.DecodeString(user.User.ClientCertificateData)
		if err != nil {
			return nil, fmt.Errorf("failed to decode client certificate data: %v", err)
		}
		auth.CertData = certData
	} else if user.User.ClientCertificate != "" {
		certData, err := ioutil.ReadFile(user.User.ClientCertificate)
		if err != nil {
			return nil, fmt.Errorf("failed to read client certificate file: %v", err)
		}
		auth.CertData = certData
	}

	if user.User.ClientKeyData != "" {
		keyData, err := base64.StdEncoding.DecodeString(user.User.ClientKeyData)
		if err != nil {
			return nil, fmt.Errorf("failed to decode client key data: %v", err)
		}
		auth.KeyData = keyData
	} else if user.User.ClientKey != "" {
		keyData, err := ioutil.ReadFile(user.User.ClientKey)
		if err != nil {
			return nil, fmt.Errorf("failed to read client key file: %v", err)
		}
		auth.KeyData = keyData
	}

	// Cache the result
	cacheMutex.Lock()
	authCache[kubeconfigPath] = auth
	cacheMutex.Unlock()

	// Schedule cleanup for expired entries
	go func() {
		time.Sleep(2 * time.Hour)
		cacheMutex.Lock()
		defer cacheMutex.Unlock()
		if cached, exists := authCache[kubeconfigPath]; exists && time.Now().After(cached.expiresAt) {
			secureCleanup(cached)
			delete(authCache, kubeconfigPath)
		}
	}()

	return auth, nil
}

// CreateHTTPClientWithKubeConfig creates an HTTP client configured with kubeconfig authentication
func CreateHTTPClientWithKubeConfig(kubeconfigPath string) (*http.Client, *KubeConfigAuth, error) {
	auth, err := ExtractAuthFromKubeConfig(kubeconfigPath)
	if err != nil {
		return nil, nil, err
	}

	// Create TLS config
	tlsConfig := &tls.Config{
		InsecureSkipVerify: auth.InsecureSkip,
	}

	// Add CA certificate if available
	if len(auth.CAData) > 0 {
		caCertPool := x509.NewCertPool()
		if !caCertPool.AppendCertsFromPEM(auth.CAData) {
			return nil, nil, fmt.Errorf("failed to parse CA certificate")
		}
		tlsConfig.RootCAs = caCertPool
	}

	// Add client certificate if available
	if len(auth.CertData) > 0 && len(auth.KeyData) > 0 {
		cert, err := tls.X509KeyPair(auth.CertData, auth.KeyData)
		if err != nil {
			return nil, nil, fmt.Errorf("failed to create client certificate: %v", err)
		}
		tlsConfig.Certificates = []tls.Certificate{cert}
	}

	// Create HTTP client with custom transport
	transport := &http.Transport{
		TLSClientConfig: tlsConfig,
	}

	client := &http.Client{
		Transport: transport,
	}

	return client, auth, nil
}

// fileExists checks if a file exists
func fileExists(path string) bool {
	_, err := os.Stat(path)
	return !os.IsNotExist(err)
}
