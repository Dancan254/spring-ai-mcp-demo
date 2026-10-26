package com.yourjavaguy.mcpserver.course;

public class CourseNotFoundException extends CourseCatalogException {

    public CourseNotFoundException(String courseId) {
        super("No course found with id '%s'".formatted(courseId));
    }
}
