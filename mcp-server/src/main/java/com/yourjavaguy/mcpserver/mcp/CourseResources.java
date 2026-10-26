package com.yourjavaguy.mcpserver.mcp;

import com.yourjavaguy.mcpserver.course.CourseCatalog;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

@Component
public class CourseResources {

    private final CourseCatalog courseCatalog;

    public CourseResources(CourseCatalog courseCatalog) {
        this.courseCatalog = courseCatalog;
    }

    @McpResource(
            uri = "course://{courseId}",
            name = "Course details",
            description = "Read-only details of a single course",
            mimeType = "text/markdown")
    public String courseDetails(String courseId) {
        var course = courseCatalog.findById(courseId);
        return """
                # %s

                - **Id:** %s
                - **Level:** %s
                - **Duration:** %d hours
                - **Capacity:** %d students

                %s
                """.formatted(
                course.title(), course.id(), course.level(),
                course.durationHours(), course.capacity(), course.description());
    }
}
