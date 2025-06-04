package com.example.kata.repository;

import com.example.kata.entity.Transaction;
import com.example.kata.enums.CategoryType;
import com.example.kata.enums.TransactionStatus;
import com.example.kata.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByUserId(Long userId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByTransactionType(TransactionType type);

    List<Transaction> findByCategory(CategoryType category);

    List<Transaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = ?1 AND t.transactionType = ?2")
    BigDecimal sumAmountByUserIdAndTransactionType(Long userId, TransactionType type);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user.id = ?1 AND t.transactionType = ?2")
    Long countByUserIdAndTransactionType(Long userId, TransactionType type);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user.id = ?1 AND t.status = ?2")
    Long countByUserIdAndStatus(Long userId, TransactionStatus status);
}