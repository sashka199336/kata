package com.example.kata.controller;

import com.example.kata.dto.request.TransactionFilterRequest;
import com.example.kata.dto.request.TransactionRequest;
import com.example.kata.dto.response.TransactionResponse;
import com.example.kata.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.kata.dto.request.TransactionUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        TransactionResponse response = transactionService.createTransaction(request, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<TransactionResponse> patchTransaction(
            @PathVariable Long id,
            @RequestBody TransactionUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        TransactionResponse response = transactionService.patchTransaction(id, request, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        TransactionResponse response = transactionService.getTransactionById(id, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<TransactionResponse> transactions = transactionService.getAllTransactions(userDetails.getUsername());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionResponse>> filterTransactions(
            @ModelAttribute TransactionFilterRequest filterRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        List<TransactionResponse> transactions = transactionService.filterTransactions(filterRequest, username);
        return ResponseEntity.ok(transactions);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        transactionService.deleteTransaction(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}