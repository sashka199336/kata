package com.example.kata.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionUpdateRequest {
    private String personType;
    private String transactionType; // <--- добавлено!
    private LocalDateTime transactionDate;
    private String comment;
    private BigDecimal amount;
    private String status;
    private String senderBank;
    private String senderAccount; // <--- добавлено!
    private String recipientBank;
    private String recipientAccount; // <--- добавлено!
    private String recipientInn;
    private String category;
    private String recipientPhone;

    // Геттеры и сеттеры для всех полей
    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderBank() {
        return senderBank;
    }

    public void setSenderBank(String senderBank) {
        this.senderBank = senderBank;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getRecipientBank() {
        return recipientBank;
    }

    public void setRecipientBank(String recipientBank) {
        this.recipientBank = recipientBank;
    }

    public String getRecipientAccount() {
        return recipientAccount;
    }

    public void setRecipientAccount(String recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

    public String getRecipientInn() {
        return recipientInn;
    }

    public void setRecipientInn(String recipientInn) {
        this.recipientInn = recipientInn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }
}