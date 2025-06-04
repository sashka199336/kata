package com.example.kata.service;

import com.example.kata.dto.request.TransactionFilterRequest;
import com.example.kata.dto.request.TransactionRequest;
import com.example.kata.dto.request.TransactionUpdateRequest;
import com.example.kata.dto.response.TransactionResponse;
import com.example.kata.entity.Transaction;

import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest request, String username);

    TransactionResponse getTransactionById(Long id, String username);

    List<TransactionResponse> getAllTransactions(String username);


    List<TransactionResponse> filterTransactions(TransactionFilterRequest filterRequest, String username);

    TransactionResponse updateTransaction(Long id, TransactionRequest request, String username);

    void deleteTransaction(Long id, String username);

    // 👉 Методы для проверки прав пользователя на редактирование и удаление транзакции
    boolean canEditTransaction(Transaction transaction, String username);

    boolean canDeleteTransaction(Transaction transaction, String username);

    // 👉 Метод PATCH (частичное обновление)
    TransactionResponse patchTransaction(Long id, TransactionUpdateRequest request, String username);

    /**
     * Получает список транзакций по фильтру
     * @param filter параметры фильтрации
     * @param username имя пользователя
     * @return отфильтрованный список транзакций
     */
    List<Transaction> getTransactionsByFilter(TransactionFilterRequest filter, String username);
}