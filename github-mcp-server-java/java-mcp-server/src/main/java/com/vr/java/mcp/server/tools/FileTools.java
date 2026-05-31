package com.vr.java.mcp.server.tools;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Component
public class FileTools {

    private static final String BASE_DIR = "C:/workspace";

    @McpTool(name = "rupesh_list_files", description = "List files inside a directory")
    public String listFiles(@McpToolParam String path) throws IOException {

        Path dir = Path.of(BASE_DIR, path).normalize();

        validatePath(dir);

        return Files.list(dir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.joining("\n"));
    }

    @McpTool(name = "rupesh_read_files",description = "Read content of a file")
    public String readFile(@McpToolParam String path) throws IOException {

        Path file = Path.of(BASE_DIR, path).normalize();

        validatePath(file);

        return Files.readString(file);
    }

    private void validatePath(Path path) {

        Path base = Path.of(BASE_DIR).normalize();

        if (!path.startsWith(base)) {
            throw new RuntimeException("Access denied");
        }
    }
}