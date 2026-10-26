package com.yourjavaguy.mcpserver;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CourseToolsIntegrationTest {

    @LocalServerPort
    int port;

    McpSyncClient mcpClient;

    @BeforeEach
    void connect() {
        var transport = HttpClientStreamableHttpTransport.builder("http://localhost:" + port).build();
        mcpClient = McpClient.sync(transport).build();
        mcpClient.initialize();
    }

    @AfterEach
    void disconnect() {
        mcpClient.closeGracefully();
    }

    @Test
    void should_expose_all_course_tools_when_client_lists_tools() {
        var toolNames = mcpClient.listTools().tools().stream().map(Tool::name).toList();

        assertThat(toolNames).containsExactlyInAnyOrder("listCourses", "searchCourses", "enrollStudent");
    }

    @Test
    void should_return_matching_course_when_searching_by_keyword() {
        var result = mcpClient.callTool(new CallToolRequest("searchCourses", Map.of("keyword", "kafka")));

        assertThat(textOf(result)).contains("Event-Driven Systems with Kafka");
    }

    @Test
    void should_return_tool_error_when_course_is_full() {
        mcpClient.callTool(enroll("spring-ai", "ada@example.com"));
        mcpClient.callTool(enroll("spring-ai", "grace@example.com"));

        var result = mcpClient.callTool(enroll("spring-ai", "linus@example.com"));

        assertThat(result.isError()).isTrue();
    }

    private static CallToolRequest enroll(String courseId, String studentEmail) {
        return new CallToolRequest("enrollStudent", Map.of("courseId", courseId, "studentEmail", studentEmail));
    }

    private static String textOf(CallToolResult result) {
        return ((TextContent) result.content().getFirst()).text();
    }
}
