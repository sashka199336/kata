package com.example.kata.service.impl;

import com.example.kata.enums.CategoryType;
import com.example.kata.dto.request.TransactionFilterRequest;
import com.example.kata.dto.response.TransactionResponse;
import com.example.kata.dto.response.dashboard.*;
import com.example.kata.service.DashboardService;
import com.example.kata.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.kata.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final TransactionService transactionService;

    @Autowired
    public DashboardServiceImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public List<PeriodCount> getTransactionsByPeriod(TransactionFilterRequest filter, String periodType, String username) {
        List<TransactionResponse> transactions = transactionService.filterTransactions(filter, username);
        Map<String, Long> grouped;

        switch (periodType.toLowerCase()) {
            case "week":
                grouped = transactions.stream()
                        .collect(Collectors.groupingBy(tx -> {
                            LocalDate date = tx.transactionDate().toLocalDate();
                            int week = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                            return date.getYear() + "-W" + week;
                        }, Collectors.counting()));
                break;
            case "month":
                grouped = transactions.stream()
                        .collect(Collectors.groupingBy(tx ->
                                        YearMonth.from(tx.transactionDate().toLocalDate()).toString(),
                                Collectors.counting()));
                break;
            case "quarter":
                grouped = transactions.stream()
                        .collect(Collectors.groupingBy(tx -> {
                            LocalDate date = tx.transactionDate().toLocalDate();
                            int quarter = (date.getMonthValue() - 1) / 3 + 1;
                            return date.getYear() + "-Q" + quarter;
                        }, Collectors.counting()));
                break;
            case "year":
                grouped = transactions.stream()
                        .collect(Collectors.groupingBy(tx ->
                                        String.valueOf(tx.transactionDate().getYear()),
                                Collectors.counting()));
                break;
            default:
                throw new IllegalArgumentException("Invalid period type: " + periodType);
        }

        return grouped.entrySet().stream()
                .map(e -> new PeriodCount(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(PeriodCount::getPeriod))
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeDynamics> getTypeByPeriod(TransactionFilterRequest filter,
                                              String periodType,
                                              String username) {
        List<Transaction> transactions = transactionService.getTransactionsByFilter(filter, username);

        Map<String, Map<String, Long>> typePeriodCounts = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> formatPeriod(transaction.getDateTime(), periodType),
                        Collectors.groupingBy(
                                tx -> tx.getType().name(),
                                Collectors.counting()
                        )
                ));

        List<TypeDynamics> result = new ArrayList<>();
        typePeriodCounts.forEach((period, typeMap) ->
                typeMap.forEach((type, count) ->
                        result.add(new TypeDynamics(period, type, count))
                )
        );

        return result;
    }

    @Override
    public List<DebitCreditComparison> compareDebitCredit(TransactionFilterRequest filter, String username) {
        List<Transaction> transactions = transactionService.getTransactionsByFilter(filter, username);

        Map<String, List<Transaction>> typeTransactions = transactions.stream()
                .collect(Collectors.groupingBy(tx -> tx.getType().name()));

        List<DebitCreditComparison> result = new ArrayList<>();
        typeTransactions.forEach((type, txList) -> {
            BigDecimal total = txList.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.add(new DebitCreditComparison(type, (long) txList.size(), total));
        });

        return result;
    }

    @Override
    public List<StatusCount> getStatusStats(TransactionFilterRequest filter, String username) {
        List<Transaction> transactions = transactionService.getTransactionsByFilter(filter, username);

        Map<String, Long> statusCounts = transactions.stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getStatus().name(),
                        Collectors.counting()
                ));

        List<StatusCount> result = new ArrayList<>();
        statusCounts.forEach((status, count) ->
                result.add(new StatusCount(status, count))
        );

        return result;
    }

    @Override
    public List<BankStats> getBankStats(TransactionFilterRequest filter, String username) {
        List<TransactionResponse> transactions = transactionService.filterTransactions(filter, username);
        List<BankStats> result = new ArrayList<>();

        // Статистика по банкам-отправителям
        Map<String, Long> senderStats = transactions.stream()
                .filter(tx -> tx.senderBank() != null && !tx.senderBank().isEmpty())
                .collect(Collectors.groupingBy(TransactionResponse::senderBank, Collectors.counting()));

        senderStats.forEach((bank, count) ->
                result.add(new BankStats(bank, "SENDER", count)));

        // Статистика по банкам-получателям
        Map<String, Long> recipientStats = transactions.stream()
                .filter(tx -> tx.recipientBank() != null && !tx.recipientBank().isEmpty())
                .collect(Collectors.groupingBy(TransactionResponse::recipientBank, Collectors.counting()));

        recipientStats.forEach((bank, count) ->
                result.add(new BankStats(bank, "RECIPIENT", count)));

        return result;
    }
    @Override
    public List<TransactionResponse> getFilteredTransactions(TransactionFilterRequest filter, String username) {
        return transactionService.filterTransactions(filter, username);
    }

    @Override
    public List<CategoryStats> getCategoryStats(TransactionFilterRequest filter, String username) {
        List<Transaction> transactions = transactionService.getTransactionsByFilter(filter, username);

        // Группировка по категории и типу
        Map<CategoryType, Map<String, List<Transaction>>> categoryTypeTransactions = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.groupingBy(
                                tx -> tx.getType().name()
                        )
                ));

        List<CategoryStats> result = new ArrayList<>();

        categoryTypeTransactions.forEach((category, typeMap) ->
                typeMap.forEach((type, txList) -> {
                    BigDecimal total = txList.stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // category может быть null, если транзакция без категории
                    String categoryName = category != null ? category.getDescription() : "Без категории";

                    result.add(new CategoryStats(categoryName, type, (long) txList.size(), total));
                })
        );

        return result;
    }

    /**
     * Форматирует дату транзакции в соответствии с указанным типом периода
     * @param dateTime дата и время транзакции
     * @param periodType тип периода (week, month, quarter, year)
     * @return строковое представление периода
     */
    private String formatPeriod(LocalDateTime dateTime, String periodType) {
        if (dateTime == null) {
            return "Unknown";
        }

        LocalDate date = dateTime.toLocalDate();

        switch (periodType.toLowerCase()) {
            case "week":
                int week = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                return date.getYear() + "-W" + week;
            case "month":
                return YearMonth.from(date).toString();
            case "quarter":
                int quarter = (date.getMonthValue() - 1) / 3 + 1;
                return date.getYear() + "-Q" + quarter;
            case "year":
                return String.valueOf(date.getYear());
            case "day":
                return date.toString(); // Format: YYYY-MM-DD
            default:
                throw new IllegalArgumentException("Invalid period type: " + periodType);
        }
    }
}