package com.yourjavaguy.mcpserver.mcp;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoursePrompts {

    @McpPrompt(name = "study-plan", description = "Build a weekly study plan from the course catalog")
    public GetPromptResult studyPlan(
            @McpArg(name = "goal", description = "What the student wants to achieve", required = true) String goal,
            @McpArg(name = "hoursPerWeek", description = "Hours the student can study each week", required = true) String hoursPerWeek) {

        var instructions = """
                I want to: %s
                I can study %s hours per week.

                Use the listCourses tool to see the catalog, pick the courses that fit my goal
                in a sensible order, and give me a week-by-week plan.
                """.formatted(goal, hoursPerWeek);

        var message = new PromptMessage(Role.USER, TextContent.builder(instructions).build());
        return GetPromptResult.builder(List.of(message))
                .description("Weekly study plan")
                .build();
    }
}
