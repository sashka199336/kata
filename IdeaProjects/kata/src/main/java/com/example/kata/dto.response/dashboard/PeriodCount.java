package com.example.kata.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodCount {
    private String period; // Например "2024-06" или "2024-W24"
    private Long count;
}