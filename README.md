# GitHub MCP Server Helm Chart

## Features

- OpenShift Route support
- Kubernetes Ingress support
- GitHub token secret management
- Readiness/Liveness probes
- OpenShift-compatible security context

## Install

```bash
helm install github-mcp-server ./github-mcp-server \
  --set github.token=<YOUR_GITHUB_TOKEN>
```

## OpenShift Route

Enabled by default.

## Kubernetes Ingress

Enable using:

```bash
helm install github-mcp-server ./github-mcp-server \
  --set ingress.enabled=true \
  --set route.enabled=false
```
