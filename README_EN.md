# ACK Cost API SDK

Alibaba Cloud ACK Cost API SDK implementation, supporting authentication via kubeconfig and querying cost allocation data in Kubernetes clusters.

## Supported Languages

| Language | Documentation | Examples |
|----------|---------------|----------|
| **Go** | [README](go/README.md) | [cost_query_example.go](go/examples/cost_query_example.go) |
| **Python** | [README](python/README.md) | [cost_query_example.py](python/examples/cost_query_example.py) |
| **Java** | [README](java/README.md) | [CostQueryExample.java](java/examples/CostQueryExample.java) |
| **JavaScript** | [README](javascript/README.md) | [cost_query_example.js](javascript/examples/cost_query_example.js) |

## API 

| API             | Description |
|-----------------|-------------|
| `Cost API`       | For querying real-time estimated costs |
| `Allocation API` | For querying cost allocation data from business bills |


## Prerequisites

- Cluster kubeconfig file (default `~/.kube/config` or specified via `KUBECONFIG` environment variable)
- Cost API service deployed in the cluster

## License

Apache 2.0 License
