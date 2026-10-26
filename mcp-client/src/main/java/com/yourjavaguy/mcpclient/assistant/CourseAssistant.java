package com.yourjavaguy.mcpclient.assistant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CourseAssistant {

    private static final String SYSTEM_PROMPT = """
            You are a friendly course advisor for an online academy.
            Always use the available tools to look up courses or enroll students.
            Never invent course ids, titles or seat counts.
            """;

    private final ChatClient chatClient;
    private final SyncMcpToolCallbackProvider mcpTools;

    public CourseAssistant(ChatClient.Builder chatClientBuilder, SyncMcpToolCallbackProvider mcpTools) {
        this.mcpTools = mcpTools;
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(mcpTools)
                .build();
    }

    public String ask(String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    public List<String> availableToolNames() {
        return Arrays.stream(mcpTools.getToolCallbacks())
                .map(toolCallback -> toolCallback.getToolDefinition().name())
                .toList();
    }
}
