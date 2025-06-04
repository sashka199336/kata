package com.example.kata.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionStatus {
    NEW("Новая"),
    CONFIRMED("Подтвержденная"),
    PROCESSING("В обработке"),
    CANCELLED("Отменена"),
    COMPLETED("Платеж выполнен"),
    DELETED("Платеж удален"),
    REFUND("Возврат");

    private final String description;

    TransactionStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static TransactionStatus fromString(String value) {
        for (TransactionStatus status : values()) {
            if (status.description.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown TransactionStatus: " + value);
    }
}