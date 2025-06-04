package com.example.kata.service;
import com.example.kata.dto.response.TransactionResponse;
import com.example.kata.dto.request.TransactionFilterRequest;
import com.example.kata.dto.response.dashboard.*;
import java.util.List;

public interface DashboardService {
    // 1. Динамика по количеству транзакций в разрезе неделя/месяц/квартал/год
    List<PeriodCount> getTransactionsByPeriod(TransactionFilterRequest filter, String periodType, String username);

    // 2. Динамика по типу транзакции
    List<TypeDynamics> getTypeByPeriod(TransactionFilterRequest filter, String periodType, String username);

    // 3. Сравнение поступивших средств и потраченных
    List<DebitCreditComparison> compareDebitCredit(TransactionFilterRequest filter, String username);

    // 4. Количество проведенных и отмененных транзакций
    List<StatusCount> getStatusStats(TransactionFilterRequest filter, String username);

    // 5. Статистика по банкам отправителя и получателей
    List<BankStats> getBankStats(TransactionFilterRequest filter, String username);

    // 6. Статистический отчет по категориям расходов и поступлений
    List<CategoryStats> getCategoryStats(TransactionFilterRequest filter, String username);
    List<TransactionResponse> getFilteredTransactions(TransactionFilterRequest filter, String username);
}