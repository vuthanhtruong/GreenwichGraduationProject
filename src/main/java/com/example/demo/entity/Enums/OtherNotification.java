package com.example.demo.entity.Enums;

public enum OtherNotification {

    // --- COMMENT NOTIFICATIONS ---
    COMMENT_MADE_ON_MAJOR_POST("Someone commented on a major class post"),
    COMMENT_MADE_ON_MINOR_POST("Someone commented on a minor class post"),
    COMMENT_MADE_ON_SPECIALIZED_POST("Someone commented on a specialized class post"),
    STUDENT_COMMENTED_ON_POST("A student commented on your class post"),

    // --- POST CREATED / UPDATED ---
    MAJOR_POST_CREATED("A new major class post was published"),
    MINOR_POST_CREATED("A new minor class post was published"),
    SPECIALIZED_POST_CREATED("A new specialized class post was published"),

    MAJOR_POST_UPDATED("A major class post was updated"),
    MINOR_POST_UPDATED("A minor class post was updated"),
    SPECIALIZED_POST_UPDATED("A specialized class post was updated"),

    // --- ASSIGNMENT SLOT ---
    MAJOR_ASSIGNMENT_SLOT_CREATED("A major assignment submission slot was created"),
    MINOR_ASSIGNMENT_SLOT_CREATED("A minor assignment submission slot was created"),
    SPECIALIZED_ASSIGNMENT_SLOT_CREATED("A specialized assignment submission slot was created"),

    MAJOR_ASSIGNMENT_SLOT_UPDATED("A major assignment submission slot was updated"),
    MINOR_ASSIGNMENT_SLOT_UPDATED("A minor assignment submission slot was updated"),
    SPECIALIZED_ASSIGNMENT_SLOT_UPDATED("A specialized assignment submission slot was updated");

    private final String description;

    OtherNotification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
