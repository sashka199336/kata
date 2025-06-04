package com.example.kata.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusCount {
    private String status; // TransactionStatus в строковом представлении
    private Long count;
}