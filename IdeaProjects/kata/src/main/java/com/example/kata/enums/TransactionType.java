package com.example.kata.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
    INCOME("Поступление"),
    EXPENSE("Списание");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static TransactionType fromString(String value) {
        for (TransactionType type : values()) {
            if (type.description.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TransactionType: " + value);
    }
}