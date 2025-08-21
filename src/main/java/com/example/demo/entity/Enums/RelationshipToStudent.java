package com.example.demo.entity.Enums;

public enum RelationshipToStudent {
    FATHER("Father to child"),
    MOTHER("Mother to child"),
    GUARDIAN("Guardian to child"),
    GRANDPARENT("Grandparent to child"),
    SIBLING("Sibling to child"),
    AUNT("Aunt to child"),
    UNCLE("Uncle to child"),
    OTHER("Other");

    private final String description;

    RelationshipToStudent(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
