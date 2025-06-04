package com.example.kata.service;

import com.example.kata.dto.response.TransactionResponse; // ← вот это обязательно!
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExcelReportService {
    public byte[] generateTransactionsReport(List<TransactionResponse> transactions) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Дата");
            header.createCell(1).setCellValue("Тип");
            header.createCell(2).setCellValue("Сумма");

            int rowIdx = 1;
            for (TransactionResponse tx : transactions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(tx.transactionDate().toString());
                row.createCell(1).setCellValue(tx.transactionType().toString());
                row.createCell(2).setCellValue(tx.amount().doubleValue());
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}