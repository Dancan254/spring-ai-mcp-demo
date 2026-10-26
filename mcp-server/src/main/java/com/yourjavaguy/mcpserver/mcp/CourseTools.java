package com.yourjavaguy.mcpserver.mcp;

import com.yourjavaguy.mcpserver.course.Course;
import com.yourjavaguy.mcpserver.course.CourseCatalog;
import com.yourjavaguy.mcpserver.course.Enrollment;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseTools {

    private final CourseCatalog courseCatalog;

    public CourseTools(CourseCatalog courseCatalog) {
        this.courseCatalog = courseCatalog;
    }

    @McpTool(name = "listCourses", description = "List every course in the catalog")
    public List<Course> listCourses() {
        return courseCatalog.findAll();
    }

    @McpTool(name = "searchCourses", description = "Find courses whose title or description contains a keyword")
    public List<Course> searchCourses(@McpToolParam(description = "Keyword to search for, e.g. 'spring' or 'kafka'", required = true) String keyword) {
        return courseCatalog.search(keyword);
    }

    @McpTool(name = "enrollStudent", description = "Enroll a student in a course. Fails if the course is full")
    public Enrollment enrollStudent(@McpToolParam(description = "Course id, e.g. 'spring-ai'", required = true) String courseId, @McpToolParam(description = "Student email address", required = true) String studentEmail) {
        return courseCatalog.enroll(courseId, studentEmail);
    }
}
