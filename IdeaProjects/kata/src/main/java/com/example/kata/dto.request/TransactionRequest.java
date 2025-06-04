package com.example.kata.dto.request;

import com.example.kata.enums.CategoryType;
import com.example.kata.enums.PersonType;
import com.example.kata.enums.TransactionType;
import com.example.kata.enums.TransactionStatus;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
        @NotNull(message = "ID пользователя обязателен")
        private Long userId;

        @NotNull(message = "Тип транзакции не может быть пустым")
        private TransactionType transactionType;

        private String comment;

        @NotNull(message = "Сумма не может быть пустой")
        @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
        private BigDecimal amount;

        @NotNull(message = "Статус транзакции не может быть пустым")
        private TransactionStatus status;

        private String senderBank;
        private String senderAccount;
        private String recipientBank;

        @Pattern(regexp = "^\\d{10,12}$", message = "ИНН должен содержать от 10 до 12 цифр")
        private String recipientInn;

        private String recipientAccount;
        private CategoryType category;

        @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Телефон должен быть в формате +7XXXXXXXXXX или 8XXXXXXXXXX")
        private String recipientPhone;

        // 👇 Вот это то, что тебе нужно!
        @NotNull(message = "Тип лица не может быть пустым")
        private PersonType personType;


}
