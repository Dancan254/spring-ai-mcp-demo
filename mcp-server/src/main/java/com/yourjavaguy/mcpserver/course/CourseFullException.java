package com.yourjavaguy.mcpserver.course;

public class CourseFullException extends CourseCatalogException {

    public CourseFullException(String courseId) {
        super("Course '%s' has no seats left".formatted(courseId));
    }
}
