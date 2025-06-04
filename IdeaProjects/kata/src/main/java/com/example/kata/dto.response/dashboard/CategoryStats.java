package com.example.kata.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStats {
    private String category; // CategoryType в строковом представлении
    private String type; // TransactionType в строковом представлении
    private Long count;
    private BigDecimal totalAmount;
}