package com.example.kata.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PersonType {
    INDIVIDUAL("Физическое лицо"),
    LEGAL_ENTITY("Юридическое лицо");

    private final String description;

    PersonType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static PersonType fromString(String value) {
        for (PersonType type : PersonType.values()) {
            if (type.description.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                "Неизвестный тип лица: " + value + ". Ожидалось: 'Физическое лицо' или 'Юридическое лицо'."
        );
    }
}