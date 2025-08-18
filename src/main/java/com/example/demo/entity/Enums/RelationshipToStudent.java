package com.example.demo.entity.Enums;

public enum RelationshipToStudent {
    FATHER,
    MOTHER,
    GUARDIAN,
    GRANDPARENT,
    SIBLING,
    AUNT,
    UNCLE,
    OTHER;

    @Override
    public String toString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}