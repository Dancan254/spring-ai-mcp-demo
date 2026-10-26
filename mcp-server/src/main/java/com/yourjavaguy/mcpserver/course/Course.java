package com.yourjavaguy.mcpserver.course;

public record Course(
        String id,
        String title,
        Level level,
        int durationHours,
        int capacity,
        String description) {

    public enum Level { BEGINNER, INTERMEDIATE, ADVANCED }
}
