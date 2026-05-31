package com.vr.java.mcp.server.tools;


import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;

public class MathTool {

    @McpTool(name = "add_nums", description = "Add two integers and return the sum")
    public int add(@McpToolParam int a, @McpToolParam int b) {
        return a + b;
    }

    @McpTool(name = "product_nums", description = "Multiply two integers and return the product")
    public int multiply(@McpToolParam int a, @McpToolParam int b) {
        return a * b;
    }
}