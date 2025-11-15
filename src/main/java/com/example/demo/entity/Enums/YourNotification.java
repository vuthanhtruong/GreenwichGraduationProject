package com.example.demo.entity.Enums;

public enum YourNotification {

    NOTIFICATION_001("You have been assigned a specialization"),
    NOTIFICATION_002("You have been added to a major class"),
    NOTIFICATION_003("You have been added to a minor class"),
    NOTIFICATION_004("You have been added to a specialization class"),
    NOTIFICATION_005("Your specialization class schedule has been updated"),
    NOTIFICATION_006("Your minor class schedule has been updated"),
    NOTIFICATION_007("Your major class schedule has been updated"),
    NOTIFICATION_008("Your major subject grade has been updated"),
    NOTIFICATION_009("Your specialization subject grade has been updated"),
    NOTIFICATION_010("Your minor subject grade has been updated"),
    NOTIFICATION_011("Your major class attendance has been updated"),
    NOTIFICATION_012("Your minor class attendance has been updated"),
    NOTIFICATION_013("Your specialization class attendance has been updated"),

    // --- New required-subject notifications ---
    NOTIFICATION_014("You have been assigned a required major subject"),
    NOTIFICATION_015("You have been assigned a required minor subject"),
    NOTIFICATION_016("You have been assigned a required specialized subject");

    private final String description;

    YourNotification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
