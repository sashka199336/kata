package com.example.kata.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeDynamics {
    private String type; // "DEBIT" или "CREDIT"
    private String period;
    private Long count;
}