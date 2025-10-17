# coding: utf-8

"""
Kubernetes kubeconfig support for client authentication
"""

import base64
import os
import yaml
from typing import Dict, List, Optional, Union
from dataclasses import dataclass


@dataclass
class KubeConfigClusterInfo:
    """Kubernetes cluster information from kubeconfig"""
    server: str
    certificate_authority_data: Optional[str] = None
    certificate_authority: Optional[str] = None
    insecure_skip_tls_verify: bool = False


@dataclass
class KubeConfigUserInfo:
    """Kubernetes user information from kubeconfig"""
    client_certificate_data: Optional[str] = None
    client_key_data: Optional[str] = None
    client_certificate: Optional[str] = None
    client_key: Optional[str] = None
    token: Optional[str] = None
    username: Optional[str] = None
    password: Optional[str] = None


@dataclass
class KubeConfigCluster:
    """Kubernetes cluster from kubeconfig"""
    name: str
    cluster: KubeConfigClusterInfo


@dataclass
class KubeConfigUser:
    """Kubernetes user from kubeconfig"""
    name: str
    user: KubeConfigUserInfo


@dataclass
class KubeConfigContextInfo:
    """Kubernetes context information from kubeconfig"""
    cluster: str
    user: str


@dataclass
class KubeConfigContext:
    """Kubernetes context from kubeconfig"""
    name: str
    context: KubeConfigContextInfo


@dataclass
class KubeConfig:
    """Kubernetes kubeconfig structure"""
    api_version: str
    kind: str
    clusters: List[KubeConfigCluster]
    users: List[KubeConfigUser]
    contexts: List[KubeConfigContext]
    current_context: str


@dataclass
class KubeConfigAuth:
    """Authentication information extracted from kubeconfig"""
    server_url: str
    cert_data: Optional[bytes] = None
    key_data: Optional[bytes] = None
    ca_data: Optional[bytes] = None
    insecure_skip: bool = False


def _file_exists(path: str) -> bool:
    """Check if a file exists"""
    return os.path.isfile(path)


def _get_default_kubeconfig_paths() -> List[str]:
    """Get default kubeconfig file paths"""
    paths = []
    
    # Check KUBECONFIG environment variable
    kubeconfig_env = os.environ.get('KUBECONFIG')
    if kubeconfig_env:
        paths.append(kubeconfig_env)
    
    # Check default location
    home_dir = os.path.expanduser('~')
    default_path = os.path.join(home_dir, '.kube', 'config')
    if _file_exists(default_path):
        paths.append(default_path)
    
    return paths


def load_kubeconfig(kubeconfig_path: Optional[str] = None) -> KubeConfig:
    """
    Load and parse a kubeconfig file
    
    :param kubeconfig_path: Path to kubeconfig file. If None, tries default locations
    :return: Parsed KubeConfig object
    :raises: ValueError if kubeconfig cannot be loaded or parsed
    """
    if kubeconfig_path is None:
        # Try default locations
        default_paths = _get_default_kubeconfig_paths()
        if not default_paths:
            raise ValueError("No kubeconfig file found in default locations")
        kubeconfig_path = default_paths[0]
    
    if not _file_exists(kubeconfig_path):
        raise ValueError(f"Kubeconfig file not found: {kubeconfig_path}")
    
    try:
        with open(kubeconfig_path, 'r') as f:
            data = yaml.safe_load(f)
    except Exception as e:
        raise ValueError(f"Failed to read kubeconfig file: {e}")
    
    try:
        # Parse clusters
        clusters = []
        for cluster_data in data.get('clusters', []):
            cluster_info = KubeConfigClusterInfo(
                server=cluster_data['cluster']['server'],
                certificate_authority_data=cluster_data['cluster'].get('certificate-authority-data'),
                certificate_authority=cluster_data['cluster'].get('certificate-authority'),
                insecure_skip_tls_verify=cluster_data['cluster'].get('insecure-skip-tls-verify', False)
            )
            clusters.append(KubeConfigCluster(
                name=cluster_data['name'],
                cluster=cluster_info
            ))
        
        # Parse users
        users = []
        for user_data in data.get('users', []):
            user_info = KubeConfigUserInfo(
                client_certificate_data=user_data['user'].get('client-certificate-data'),
                client_key_data=user_data['user'].get('client-key-data'),
                client_certificate=user_data['user'].get('client-certificate'),
                client_key=user_data['user'].get('client-key'),
                token=user_data['user'].get('token'),
                username=user_data['user'].get('username'),
                password=user_data['user'].get('password')
            )
            users.append(KubeConfigUser(
                name=user_data['name'],
                user=user_info
            ))
        
        # Parse contexts
        contexts = []
        for context_data in data.get('contexts', []):
            context_info = KubeConfigContextInfo(
                cluster=context_data['context']['cluster'],
                user=context_data['context']['user']
            )
            contexts.append(KubeConfigContext(
                name=context_data['name'],
                context=context_info
            ))
        
        return KubeConfig(
            api_version=data.get('apiVersion', ''),
            kind=data.get('kind', ''),
            clusters=clusters,
            users=users,
            contexts=contexts,
            current_context=data.get('current-context', '')
        )
    
    except Exception as e:
        raise ValueError(f"Failed to parse kubeconfig: {e}")


def extract_auth_from_kubeconfig(kubeconfig_path: Optional[str] = None) -> KubeConfigAuth:
    """
    Extract authentication information from kubeconfig
    
    :param kubeconfig_path: Path to kubeconfig file. If None, tries default locations
    :return: KubeConfigAuth object with authentication information
    :raises: ValueError if authentication info cannot be extracted
    """
    config = load_kubeconfig(kubeconfig_path)
    
    # Find current context
    current_context = None
    for ctx in config.contexts:
        if ctx.name == config.current_context:
            current_context = ctx
            break
    
    if current_context is None:
        raise ValueError(f"Current context not found: {config.current_context}")
    
    # Find cluster info
    cluster = None
    for c in config.clusters:
        if c.name == current_context.context.cluster:
            cluster = c
            break
    
    if cluster is None:
        raise ValueError(f"Cluster not found: {current_context.context.cluster}")
    
    # Find user info
    user = None
    for u in config.users:
        if u.name == current_context.context.user:
            user = u
            break
    
    if user is None:
        raise ValueError(f"User not found: {current_context.context.user}")
    
    auth = KubeConfigAuth(
        server_url=cluster.cluster.server,
        insecure_skip=cluster.cluster.insecure_skip_tls_verify
    )
    
    # Extract CA data
    if cluster.cluster.certificate_authority_data:
        try:
            auth.ca_data = base64.b64decode(cluster.cluster.certificate_authority_data)
        except Exception as e:
            raise ValueError(f"Failed to decode CA data: {e}")
    elif cluster.cluster.certificate_authority:
        try:
            with open(cluster.cluster.certificate_authority, 'rb') as f:
                auth.ca_data = f.read()
        except Exception as e:
            raise ValueError(f"Failed to read CA file: {e}")
    
    # Extract client certificate and key
    if user.user.client_certificate_data:
        try:
            auth.cert_data = base64.b64decode(user.user.client_certificate_data)
        except Exception as e:
            raise ValueError(f"Failed to decode client certificate data: {e}")
    elif user.user.client_certificate:
        try:
            with open(user.user.client_certificate, 'rb') as f:
                auth.cert_data = f.read()
        except Exception as e:
            raise ValueError(f"Failed to read client certificate file: {e}")
    
    if user.user.client_key_data:
        try:
            auth.key_data = base64.b64decode(user.user.client_key_data)
        except Exception as e:
            raise ValueError(f"Failed to decode client key data: {e}")
    elif user.user.client_key:
        try:
            with open(user.user.client_key, 'rb') as f:
                auth.key_data = f.read()
        except Exception as e:
            raise ValueError(f"Failed to read client key file: {e}")
    
    return auth
