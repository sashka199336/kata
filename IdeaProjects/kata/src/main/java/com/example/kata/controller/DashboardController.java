package com.example.kata.controller;

import com.example.kata.dto.request.TransactionFilterRequest;
import com.example.kata.dto.response.dashboard.*;
import com.example.kata.service.DashboardService;
import com.example.kata.service.ExcelReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.kata.dto.response.TransactionResponse;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ExcelReportService excelReportService;

    @Autowired
    public DashboardController(DashboardService dashboardService, ExcelReportService excelReportService) {
        this.dashboardService = dashboardService;
        this.excelReportService = excelReportService;
    }

    // 1. Динамика по количеству транзакций
    @GetMapping("/period")
    public List<PeriodCount> getPeriodDashboard(
            @ModelAttribute TransactionFilterRequest filter,
            @RequestParam(defaultValue = "month") String periodType,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        return dashboardService.getTransactionsByPeriod(filter, periodType, username);
    }

    // 2. Динамика по типу транзакции
    @GetMapping("/type-period")
    public List<TypeDynamics> getTypePeriodDashboard(
            @ModelAttribute TransactionFilterRequest filter,
            @RequestParam(defaultValue = "month") String periodType,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        return dashboardService.getTypeByPeriod(filter, periodType, username);
    }

    // 3. Сравнение поступивших и потраченных
    @GetMapping("/debit-credit")
    public List<DebitCreditComparison> getDebitCreditDashboard(
            @ModelAttribute TransactionFilterRequest filter,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        return dashboardService.compareDebitCredit(filter, username);
    }

    // 4. Количество проведённых и отменённых транзакций
    @GetMapping("/status")
    public List<StatusCount> getStatusDashboard(
            @ModelAttribute TransactionFilterRequest filter,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        return dashboardService.getStatusStats(filter, username);
    }

    // 5. Статистика по банкам
    @GetMapping("/banks")
    public List<BankStats> getBanksDashboard(
            @ModelAttribute TransactionFilterRequest filter,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        return dashboardService.getBankStats(filter, username);
    }

    // 6. Статистика по категориям расходов и поступлений
    @GetMapping("/categories")
    public List<CategoryStats> getCategoriesDashboard(
            @ModelAttribute TransactionFilterRequest filter,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        return dashboardService.getCategoryStats(filter, username);
    }

    // 7. Экспорт отчёта в Excel
    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> downloadExcelReport(
            @ModelAttribute TransactionFilterRequest filter,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws Exception {
        String username = userDetails.getUsername();
        // ⚡ Здесь предполагается, что dashboardService.getFilteredTransactions(filter, username)
        // возвращает List<TransactionResponse> по фильтру.
        List<TransactionResponse> data = dashboardService.getFilteredTransactions(filter, username);

        byte[] content = excelReportService.generateTransactionsReport(data);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=transactions.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(content);
    }
}