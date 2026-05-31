# Java MCP Server

A Spring Boot-based Model Context Protocol (MCP) server implementation in Java, providing a bridge between AI/LLM applications and Java services.

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Prerequisites](#prerequisites)
4. [Project Structure](#project-structure)
5. [Build & Run](#build--run)
6. [Configuration](#configuration)
7. [MCP Server Configuration Guide](#mcp-server-configuration-guide)
8. [Health & Metrics](#health--metrics)
9. [Dependencies](#dependencies)
10. [Testing](#testing)
11. [MCP Protocol](#mcp-protocol)
12. [Development](#development)
13. [Troubleshooting](#troubleshooting)
14. [Version History](#version-history)
15. [Future Enhancements](#future-enhancements)
16. [References](#references)

---

## Overview

This project implements an MCP server using Spring Boot and Spring AI. It exposes tools and resources to LLM clients through the Model Context Protocol, enabling AI applications to interact with Java services.

The server can run in two modes:
- **HTTP Mode** - As a web service on localhost:8080
- **Stdio Mode** - As an embedded subprocess in VS Code

## Features

- **Spring Boot 4.0.6** - Modern Java web framework
- **Spring AI MCP Server** - Native MCP server implementation
- **Dual Mode Support** - HTTP and Stdio/subprocess modes
- **Spring Actuator** - Built-in health checks and metrics
- **Maven-based Build** - Standard Java project structure
- **Zero Port Conflicts** - Stdio mode avoids port management issues

## Prerequisites

- **Java 17** (LTS)
- **Maven 3.6+** (or use included mvnw/mvnw.cmd)

## Project Structure

```
java-mcp-server/
├── src/
│   ├── main/
│   │   ├── java/com/vr/java/mcp/server/
│   │   │   └── JavaMcpServerApplication.java    # Main Spring Boot application
│   │   └── resources/
│   │       └── application.yaml                   # Spring configuration
│   └── test/
│       └── java/com/vr/java/mcp/server/
│           └── JavaMcpServerApplicationTests.java # Unit tests
├── pom.xml                                         # Maven configuration
├── mvnw / mvnw.cmd                                # Maven wrapper scripts
└── HELP.md                                         # Spring Boot help
```

---

## Build & Run

### Build

Build the project using Maven:

```bash
# Using mvnw wrapper (recommended)
./mvnw clean package

# Or using system Maven
mvn clean package
```

The built JAR will be at: `target/java-mcp-server-0.0.1-SNAPSHOT.jar`

### Run - HTTP Mode

Start the MCP server as a traditional web service:

```bash
# Using mvnw wrapper
./mvnw spring-boot:run

# Or using java directly (after building)
java -jar target/java-mcp-server-0.0.1-SNAPSHOT.jar
```

The server will start on the default Spring Boot port (typically `http://localhost:8080/mcp`).

### Run - Stdio Mode

The server runs in stdio mode through VS Code configuration. See [MCP Server Configuration Guide](#mcp-server-configuration-guide).

---

## Configuration

### Application Properties/YAML

Configuration is managed through `src/main/resources/application.yaml`.

#### HTTP Mode (Default)

```yaml
server:
  port: 8080
  servlet:
    context-path: /mcp

spring:
  application:
    name: java-mcp-server
    description: Java MCP Server for AI integrations
  
  main:
    web-application-type: servlet

logging:
  level:
    root: INFO
    org.springframework: WARN
```

#### Stdio Mode (For VS Code Integration)

```yaml
spring:
  application:
    name: java-mcp-server
    description: Java MCP Server for AI integrations
  
  ai:
    mcp:
      server:
        stdio: true  # Enable stdio mode
  
  main:
    web-application-type: none  # Disable web server

logging:
  level:
    root: WARN
    org.springframework: WARN
    org.springframework.ai: INFO
  pattern:
    console: ""  # No formatting for stdio
```

Alternatively, using `application.properties`:

```properties
# Stdio Mode Configuration
spring.ai.mcp.server.stdio=true
spring.main.web-application-type=none
logging.level.root=WARN
logging.pattern.console=

# Application Metadata
spring.application.name=java-mcp-server
spring.application.description=Java MCP Server for AI integrations
```

---

## MCP Server Configuration Guide

### Overview

MCP servers can be configured in two ways:
1. **HTTP Mode** - Server runs on HTTP endpoint (useful for existing web services)
2. **Command Mode** - Server runs as a subprocess via stdio (useful for VS Code integration)

### Configuration File: `.vscode/mcp.json`

The MCP server configuration is defined in `.vscode/mcp.json` at the VS Code workspace root.

#### File Structure

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

### HTTP-Based MCP Server

#### Use Case
Running an existing web service as an MCP server (e.g., Spring Boot application on localhost:8080)

#### Configuration Example

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

#### Required Properties

| Property | Type   | Description |
|----------|--------|-------------|
| `url`    | string | HTTP endpoint of the MCP server |
| `type`   | string | Must be `"http"` |

#### Optional Properties

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

#### Use Case Examples

- Spring Boot MCP Server running on HTTP
- Node.js Express MCP Server
- Python FastAPI MCP Server
- Any language/framework providing HTTP MCP endpoint

---

### Command-Based MCP Server (Stdio Mode)

#### Use Case
Running a server as a subprocess, communicating via stdio (recommended for custom tools and VS Code integration)

#### Configuration Example

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

#### Required Properties

| Property  | Type   | Description |
|-----------|--------|-------------|
| `command` | string | Executable to run (java, node, python, etc.) |
| `args`    | array  | Arguments to pass to the command |

#### Optional Properties

```json
{
  "servers": {
    "rupesh_file_tools": {
      "command": "java",
      "args": [...],
      "env": {
        "LOG_LEVEL": "DEBUG",
        "CUSTOM_VAR": "value"
      }
    }
  }
}
```

#### Use Case Examples

- Java MCP Server (Spring Boot via JAR)
- Node.js MCP Server (via node command)
- Python MCP Server (via python command)
- Custom bash/shell scripts
- Compiled binaries (Go, Rust, C++, etc.)

---

### Working Configuration Examples

#### Example 1: Both HTTP and Command-Based Servers

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

#### Example 2: Multiple Java Servers

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

### Comparison: HTTP vs Command Mode

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

### Development Workflow

#### Building the Server

```bash
# Clean and build
mvn clean package

# The built JAR will be at:
# target/java-mcp-server-0.0.1-SNAPSHOT.jar
```

#### Starting in HTTP Mode (Manual)

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

#### Starting in Stdio Mode (Automatic via VS Code)

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

## Health & Metrics

Spring Actuator endpoints are available (HTTP mode):

- **Health Check**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Info**: `GET /actuator/info`

Enable in `application.yaml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

---

## Dependencies

### Core
- `spring-boot-starter-web` - Web MVC framework
- `spring-boot-starter-actuator` - Monitoring and management
- `spring-ai-starter-mcp-server-webmvc` - MCP server implementation

### Testing
- `spring-boot-starter-test` - Spring Boot testing support

---

## Testing

Run the test suite:

```bash
./mvnw test
```

---

## MCP Protocol

This server implements the Model Context Protocol, allowing LLM/AI applications to:
- Call exposed tools/functions
- Access resources managed by the server
- Receive structured responses

For more information, visit [modelcontextprotocol.io](https://modelcontextprotocol.io/)

---

## Development

### IDE Setup

For IntelliJ IDEA or other IDEs with Maven support:
1. Open project root
2. IDE should auto-detect Maven configuration
3. Build and run configurations will be generated

### Adding New Tools

Tools can be added by creating new Spring components and exposing them through the MCP framework. Example:

```java
@Component
public class MyTool {
    // Tool implementation
}
```

---

## Troubleshooting

### Port Already in Use (HTTP Mode)
If port 8080 is in use, specify a different port:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
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

### Build Issues
Clean and rebuild:

```bash
./mvnw clean package -U
```

The `-U` flag forces Maven to update dependencies.

### Stdio Server Fails to Start (Stdio Mode)

1. Verify JAR path is correct (use absolute path)
2. Check Java is installed: `java -version`
3. Verify JAR exists: `ls -la path/to/jar`
4. Test JAR locally first with HTTP mode
5. Check logging configuration - ensure `logging.pattern.console` is empty

### Empty Output or Errors (Stdio Mode)

Check `application.yaml` logging configuration:
- Ensure `logging.level.root` is set to WARN or higher
- Ensure `logging.pattern.console` is empty (no formatting)
- Ensure `spring.main.web-application-type` is set to `none`

---

## Version History

- **0.0.1-SNAPSHOT** - Initial implementation
  - Java 17
  - Spring Boot 4.0.6
  - Spring AI 2.0.0-M8
  - Dual mode support (HTTP and Stdio)

---

## Future Enhancements

- [ ] Add custom MCP tools
- [ ] Implement resource providers
- [ ] Add comprehensive error handling
- [ ] Add advanced logging configuration
- [ ] Add Docker support
- [ ] Add CI/CD pipeline
- [ ] Add WebSocket mode support
- [ ] Add performance monitoring

---

## References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AI Documentation](https://spring.io/projects/spring-ai)
- [Model Context Protocol](https://modelcontextprotocol.io/)
- [Maven Documentation](https://maven.apache.org/)
- [VS Code MCP Configuration](https://code.visualstudio.com/docs/copilot/mcp)

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

## License

Specify your project license here.

## Contact

For questions or issues, please reach out to the project maintainer.
