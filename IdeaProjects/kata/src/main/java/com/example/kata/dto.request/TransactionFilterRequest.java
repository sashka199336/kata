package com.example.kata.dto.request;

import com.example.kata.enums.CategoryType;
import com.example.kata.enums.TransactionStatus;
import com.example.kata.enums.TransactionType;
import com.example.kata.enums.PersonType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionFilterRequest(
        String senderBank,
        String senderAccount,
        String recipientBank,
        String recipientAccount,
        String recipientInn,
        String recipientPhone,
        TransactionType transactionType,
        TransactionStatus status,
        CategoryType category,
        String comment,
        BigDecimal amountFrom,
        BigDecimal amountTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime dateFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime dateTo,
        PersonType personType,
        Long userId
) {}