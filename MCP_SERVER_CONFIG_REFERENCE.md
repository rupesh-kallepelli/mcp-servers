# MCP Server Configuration Reference

This document provides a comprehensive guide for configuring Model Context Protocol (MCP) servers in VS Code, covering both HTTP and command-based server types.

## Overview

MCP servers can be configured in two ways:
1. **HTTP Mode** - Server runs on HTTP endpoint (useful for existing web services)
2. **Command Mode** - Server runs as a subprocess via stdio (useful for custom tools)

---

## Configuration File: `.vscode/mcp.json`

The MCP server configuration is defined in `.vscode/mcp.json` at the workspace root.

### File Structure

```json
{
  "servers": {
    // Server configurations go here
  },
  "inputs": [
    // Input prompts for sensitive data (like tokens)
  ]
}
```

---

## HTTP-Based MCP Server

### Use Case
Running an existing web service as an MCP server (e.g., Spring Boot application on localhost:8080)

### Configuration Example

```json
{
  "servers": {
    "my_file_tools": {
      "url": "http://localhost:8080/mcp",
      "type": "http"
    }
  }
}
```

### Required Properties

| Property | Type   | Description |
|----------|--------|-------------|
| `url`    | string | HTTP endpoint of the MCP server |
| `type`   | string | Must be `"http"` |

### Optional Properties

```json
{
  "servers": {
    "my_file_tools": {
      "url": "http://localhost:8080/mcp",
      "type": "http",
      "headers": {
        "Authorization": "Bearer YOUR_TOKEN",
        "Content-Type": "application/json"
      }
    }
  }
}
```

### Use Case Examples

- Spring Boot MCP Server running on HTTP
- Node.js Express MCP Server
- Python FastAPI MCP Server
- Any language/framework providing HTTP MCP endpoint

---

## Command-Based MCP Server

### Use Case
Running a server as a subprocess, communicating via stdio (recommended for custom tools)

### Configuration Example

```json
{
  "servers": {
    "rupesh_file_tools": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "C:/Users/kalle/development/mcp-servers/github-mcp-server-java/java-mcp-server/target/java-mcp-server-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

### Required Properties

| Property  | Type   | Description |
|-----------|--------|-------------|
| `command` | string | Executable to run (java, node, python, etc.) |
| `args`    | array  | Arguments to pass to the command |

### Optional Properties

```json
{
  "servers": {
    "rupesh_file_tools": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "C:/Users/kalle/development/mcp-servers/github-mcp-server-java/java-mcp-server/target/java-mcp-server-0.0.1-SNAPSHOT.jar"
      ],
      "env": {
        "LOG_LEVEL": "DEBUG",
        "CUSTOM_VAR": "value"
      }
    }
  }
}
```

### Use Case Examples

- Java MCP Server (Spring Boot via JAR)
- Node.js MCP Server (via node command)
- Python MCP Server (via python command)
- Custom bash/shell scripts
- Compiled binaries (Go, Rust, C++, etc.)

---

## Spring Boot MCP Server Configuration

### Application Properties/YAML

When running a Spring Boot MCP server, configure it via `application.properties` or `application.yaml`.

#### HTTP Mode (Default)

**application.yaml**
```yaml
server:
  port: 8080
  servlet:
    context-path: /mcp

spring:
  application:
    name: java-mcp-server
  ai:
    mcp:
      server:
        # Server runs on HTTP (default)
```

**.vscode/mcp.json**
```json
{
  "servers": {
    "my_file_tools": {
      "url": "http://localhost:8080/mcp",
      "type": "http"
    }
  }
}
```

#### Stdio Mode (Embedded in VS Code)

**application.yaml**
```yaml
spring:
  application:
    name: java-mcp-server
  ai:
    mcp:
      server:
        stdio: true  # Enable stdio mode

  main:
    web-application-type: none  # Disable web server

logging:
  level:
    root: WARN  # Reduce console output for stdio
  pattern:
    console: ""  # No console output format (stdio only)
```

**.vscode/mcp.json**
```json
{
  "servers": {
    "rupesh_file_tools": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "path/to/java-mcp-server-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

---

## Working Configuration Examples

### Example 1: Both HTTP and Command-Based Servers

```json
{
  "servers": {
    "my_file_tools": {
      "url": "http://localhost:8080/mcp",
      "type": "http"
    },
    "rupesh_file_tools": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "C:/Users/kalle/development/mcp-servers/github-mcp-server-java/java-mcp-server/target/java-mcp-server-0.0.1-SNAPSHOT.jar"
      ]
    },
    "github": {
      "type": "http",
      "url": "https://github-mcp-server-example.com/mcp",
      "headers": {
        "Authorization": "Bearer ${input:github_mcp_pat}"
      }
    }
  },
  "inputs": [
    {
      "type": "promptString",
      "id": "github_mcp_pat",
      "description": "GitHub Personal Access Token",
      "password": true
    }
  ]
}
```

### Example 2: Multiple Java Servers

```json
{
  "servers": {
    "file_tools": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "C:/servers/file-tools/target/file-tools-1.0.jar"
      ]
    },
    "database_tools": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "C:/servers/db-tools/target/db-tools-1.0.jar"
      ]
    },
    "api_tools": {
      "url": "http://localhost:3000/mcp",
      "type": "http"
    }
  }
}
```

---

## Comparison: HTTP vs Command Mode

| Feature | HTTP Mode | Command Mode |
|---------|-----------|--------------|
| **Startup** | Service must be running externally | Starts automatically via VS Code |
| **Resources** | Shared with HTTP server | Isolated process |
| **Port Management** | Manual port management | No port conflicts |
| **Logging** | External logs | Integrated with stdio |
| **Debugging** | Standalone debugging tools | Can debug via IDE if supported |
| **Development** | Better for testing running services | Better for development/embedded tools |
| **Deployment** | Requires separate deployment | Embedded in extension |
| **Performance** | Minimal overhead (HTTP) | Process startup overhead |

---

## Spring Boot MCP Server Application Properties

### Recommended Configuration for Stdio Mode

**application.properties**
```properties
# MCP Server Configuration
spring.ai.mcp.server.stdio=true

# Disable Web Server
spring.main.web-application-type=none

# Logging - Reduce output for stdio
logging.level.root=WARN
logging.level.org.springframework=WARN
logging.level.org.springframework.ai=INFO
logging.pattern.console=

# Application Metadata
spring.application.name=java-mcp-server
spring.application.description=Java MCP Server for AI integrations

# Management (optional)
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

**application.yaml**
```yaml
spring:
  application:
    name: java-mcp-server
    description: Java MCP Server for AI integrations
  
  ai:
    mcp:
      server:
        stdio: true
  
  main:
    web-application-type: none

logging:
  level:
    root: WARN
    org.springframework: WARN
    org.springframework.ai: INFO
  pattern:
    console: ""

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized
```

---

## Development Workflow

### Building the Java MCP Server

```bash
# Clean and build
mvn clean package

# The built JAR will be at:
# target/java-mcp-server-0.0.1-SNAPSHOT.jar
```

### Starting in HTTP Mode (Manual)

```bash
java -jar target/java-mcp-server-0.0.1-SNAPSHOT.jar
```

Then configure in `.vscode/mcp.json`:
```json
{
  "servers": {
    "my_tool": {
      "url": "http://localhost:8080/mcp",
      "type": "http"
    }
  }
}
```

### Starting in Stdio Mode (Automatic)

Just configure in `.vscode/mcp.json`, and VS Code will start it automatically:
```json
{
  "servers": {
    "my_tool": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.stdio=true",
        "-Dspring.main.web-application-type=none",
        "-Dlogging.pattern.console=",
        "-jar",
        "target/java-mcp-server-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

---

## Troubleshooting

### HTTP Server Issues

**Problem**: Port 8080 already in use
```bash
# Use a different port
java -jar target/java-mcp-server-0.0.1-SNAPSHOT.jar --server.port=8081
```

Update `.vscode/mcp.json`:
```json
{
  "servers": {
    "my_tool": {
      "url": "http://localhost:8081/mcp",
      "type": "http"
    }
  }
}
```

### Command Server Issues

**Problem**: Server fails to start
1. Verify JAR path is correct (absolute path recommended)
2. Check Java is installed: `java -version`
3. Verify JAR exists: `ls -la path/to/jar`
4. Test JAR locally first

**Problem**: Empty output or errors
Check `application.yaml` logging configuration - ensure `logging.pattern.console` is empty for stdio mode.

---

## Best Practices

1. **Use Command Mode for Development** - Easier startup, no port conflicts
2. **Use HTTP Mode for Existing Services** - Don't rebuild if service already runs
3. **Set Logging to WARN in Stdio Mode** - Reduces noise in communications
4. **Use Absolute Paths in Command Args** - Prevents path resolution issues
5. **Test Locally First** - Verify JAR/server works before configuring
6. **Use Environment Variables** - For sensitive data, use `inputs` section instead
7. **Keep Configs in Git** - Commit `.vscode/mcp.json` (without secrets)

---

## Resources

- [Model Context Protocol Documentation](https://modelcontextprotocol.io/)
- [Spring AI MCP Server Documentation](https://spring.io/projects/spring-ai)
- [VS Code MCP Configuration](https://code.visualstudio.com/docs/copilot/mcp)

