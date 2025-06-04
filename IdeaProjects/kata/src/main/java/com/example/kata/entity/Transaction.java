package com.example.kata.entity;

import com.example.kata.enums.CategoryType;
import com.example.kata.enums.PersonType;
import com.example.kata.enums.TransactionStatus;
import com.example.kata.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "person_type", nullable = false)
    private PersonType personType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "comment")
    private String comment;

    @Column(name = "sender_bank")
    private String senderBank;

    @Column(name = "recipient_bank")
    private String recipientBank;

    @Column(name = "sender_account")
    private String senderAccount;

    @Column(name = "recipient_inn")
    private String recipientInn;

    @Column(name = "recipient_account")
    private String recipientAccount;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Метод-обертка для получения даты транзакции
     * @return дата и время транзакции
     */
    public LocalDateTime getDateTime() {
        return this.transactionDate;
    }

    /**
     * Метод-алиас для получения типа транзакции
     * @return тип транзакции
     */
    public TransactionType getType() {
        return this.transactionType;
    }
}