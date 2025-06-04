package com.example.kata.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryType {
    // Категории доходов
    SALARY("Зарплата"),
    DIVIDEND("Дивиденды"),
    INTEREST("Проценты по вкладам"),
    RENTAL("Аренда"),
    GIFT("Подарок"),
    OTHER_INCOME("Прочие доходы"),

    // Категории расходов
    FOOD("Продукты"),
    HOUSING("Жилье"),
    TRANSPORTATION("Транспорт"),
    ENTERTAINMENT("Развлечения"),
    HEALTHCARE("Здравоохранение"),
    EDUCATION("Образование"),
    CLOTHING("Одежда"),
    UTILITIES("Коммунальные услуги"),
    DEBT_PAYMENT("Выплата долга"),
    SAVINGS("Сбережения"),
    CHARITY("Благотворительность"),
    OTHER_EXPENSE("Прочие расходы");

    private final String description;

    CategoryType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static CategoryType fromString(String value) {
        for (CategoryType category : values()) {
            if (category.description.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}