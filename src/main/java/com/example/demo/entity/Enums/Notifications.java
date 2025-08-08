package com.example.demo.entity.Enums;

public enum Notifications {
    NOTIFICATION_001("Default event description"),
    NOTIFICATION_002("You have a new message from another user."),
    NOTIFICATION_003("You have new feedback from a student."),
    NOTIFICATION_004("A new post has been made in your classroom."),
    NOTIFICATION_005("A new document has been shared with you."),
    NOTIFICATION_006("Someone has commented on your post."),
    NOTIFICATION_007("A new blog post has been published."),
    NOTIFICATION_008("You have a new notification related to your schedule."),
    NOTIFICATION_009("You have been added to a new classroom."),
    NOTIFICATION_010("Default system event.");
    private final String description;

    Notifications(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}