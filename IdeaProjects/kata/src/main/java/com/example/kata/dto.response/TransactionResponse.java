package com.example.kata.dto.response;

import com.example.kata.enums.CategoryType;
import com.example.kata.enums.PersonType;
import com.example.kata.enums.TransactionStatus;
import com.example.kata.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        PersonType personType,
        LocalDateTime transactionDate,
        TransactionType transactionType,
        String comment,
        BigDecimal amount,
        TransactionStatus status,
        String senderBank,
        String senderAccount,
        String recipientBank,
        String recipientInn,
        String recipientAccount,
        CategoryType category,
        String recipientPhone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}