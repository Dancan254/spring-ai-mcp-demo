package com.yourjavaguy.mcpserver.course;

import com.yourjavaguy.mcpserver.course.Course.Level;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CourseCatalog {

    private final Map<String, Course> courses = Map.of(
            "java-101", new Course("java-101", "Java Fundamentals", Level.BEGINNER, 12, 30,
                    "Variables, control flow, classes, records and collections."),
            "spring-boot-4", new Course("spring-boot-4", "Spring Boot 4 in Practice", Level.INTERMEDIATE, 20, 25,
                    "Build production REST APIs with Spring Boot 4, Flyway and Testcontainers."),
            "spring-ai", new Course("spring-ai", "Building AI Apps with Spring AI", Level.INTERMEDIATE, 10, 2,
                    "ChatClient, tool calling, RAG and the Model Context Protocol."),
            "jvm-internals", new Course("jvm-internals", "JVM Internals", Level.ADVANCED, 15, 15,
                    "Class loading, JIT compilation, garbage collectors and virtual threads."),
            "kafka", new Course("kafka", "Event-Driven Systems with Kafka", Level.ADVANCED, 18, 20,
                    "Topics, partitions, consumer groups and exactly-once processing."));

    private final Map<String, Set<String>> enrolledStudentsByCourse = new HashMap<>();

    public List<Course> findAll() {
        return courses.values().stream()
                .sorted(Comparator.comparing(Course::id))
                .toList();
    }

    public List<Course> search(String keyword) {
        var lowerCaseKeyword = keyword.toLowerCase();
        return findAll().stream()
                .filter(course -> course.title().toLowerCase().contains(lowerCaseKeyword)
                        || course.description().toLowerCase().contains(lowerCaseKeyword))
                .toList();
    }

    public Course findById(String courseId) {
        var course = courses.get(courseId);
        if (course == null) {
            throw new CourseNotFoundException(courseId);
        }
        return course;
    }

    // synchronized because many MCP clients can enroll at the same time
    public synchronized Enrollment enroll(String courseId, String studentEmail) {
        var course = findById(courseId);
        var enrolledStudents = enrolledStudentsByCourse.computeIfAbsent(courseId, id -> new HashSet<>());

        if (!enrolledStudents.contains(studentEmail) && enrolledStudents.size() >= course.capacity()) {
            throw new CourseFullException(courseId);
        }

        enrolledStudents.add(studentEmail);
        return new Enrollment(course.id(), course.title(), studentEmail, course.capacity() - enrolledStudents.size());
    }
}
