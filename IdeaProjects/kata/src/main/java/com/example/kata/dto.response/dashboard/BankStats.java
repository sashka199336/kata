package com.example.kata.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankStats {
    private String bankName;
    private String role; // "SENDER" или "RECIPIENT"
    private Long count;
}