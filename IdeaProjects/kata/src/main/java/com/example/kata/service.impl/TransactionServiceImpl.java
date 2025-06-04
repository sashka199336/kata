package com.example.kata.service.impl;

import com.example.kata.dto.request.TransactionFilterRequest;
import com.example.kata.dto.request.TransactionRequest;
import com.example.kata.dto.response.TransactionResponse;
import com.example.kata.entity.IntegrationLog;
import com.example.kata.entity.Transaction;
import com.example.kata.entity.User;
import com.example.kata.dto.request.TransactionUpdateRequest;
import com.example.kata.enums.TransactionStatus;
import com.example.kata.exception.ResourceNotFoundException;
import com.example.kata.repository.IntegrationLogRepository;
import com.example.kata.repository.TransactionRepository;
import com.example.kata.service.TransactionService;
import com.example.kata.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.kata.enums.CategoryType;
import com.example.kata.enums.PersonType;
import com.example.kata.enums.TransactionType;
import com.example.kata.enums.TransactionStatus;
import lombok.Data;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.example.kata.exception.ResourceNotFoundException;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final IntegrationLogRepository integrationLogRepository;
    private final ObjectMapper objectMapper;


    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, String username) {
        // Получаем московское время
        LocalDateTime moscowNow = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

        // Попробуем найти пользователя по имени
        User user = userService.findByUsername(username).orElseGet(() -> {
            // Если пользователь не найден, создаём нового пользователя
            User defaultUser = new User();
            defaultUser.setUsername(username);
            defaultUser.setEmail("default@example.com");
            defaultUser.setFirstName("Default");
            defaultUser.setLastName("User");

            // Сохраняем нового пользователя в базе данных
            return userService.save(defaultUser);
        });

        // Создаём новую транзакцию
        Transaction transaction = new Transaction();
        transaction.setUser(user); // Связываем транзакцию с пользователем
        transaction.setPersonType(request.getPersonType());
        transaction.setTransactionDate(moscowNow);
        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(request.getStatus());
        transaction.setComment(request.getComment());
        transaction.setSenderBank(request.getSenderBank());
        transaction.setSenderAccount(request.getSenderAccount());
        transaction.setRecipientBank(request.getRecipientBank());
        transaction.setRecipientInn(request.getRecipientInn());
        transaction.setRecipientAccount(request.getRecipientAccount());
        transaction.setCategory(request.getCategory());
        transaction.setRecipientPhone(request.getRecipientPhone());

        // Сохраняем транзакцию в базе данных
        transaction = transactionRepository.save(transaction);

        // Преобразуем сущность в DTO
        return mapToTransactionResponse(transaction);
    }

    @Override
    public TransactionResponse getTransactionById(Long id, String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }

        return mapToTransactionResponse(transaction);
    }


    @Override
    public List<TransactionResponse> getAllTransactions(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }
    @Override
    public TransactionResponse patchTransaction(Long id, TransactionUpdateRequest request, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Транзакция не найдена"));

        // Запрещённые статусы
        Set<TransactionStatus> blockedStatuses = Set.of(
                TransactionStatus.CONFIRMED,
                TransactionStatus.PROCESSING,
                TransactionStatus.CANCELLED,
                TransactionStatus.COMPLETED,
                TransactionStatus.DELETED,
                TransactionStatus.REFUND
        );

        if (blockedStatuses.contains(transaction.getStatus())) {
            throw new RuntimeException("Редактирование запрещено для данного статуса транзакции");
        }

        // Обновляем только разрешённые поля:
        if (request.getPersonType() != null) {
            transaction.setPersonType(PersonType.fromString(request.getPersonType()));
        }
        if (request.getComment() != null) {
            transaction.setComment(request.getComment());
        }
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getStatus() != null) {
            transaction.setStatus(TransactionStatus.fromString(request.getStatus()));
        }
        if (request.getRecipientBank() != null) {
            transaction.setRecipientBank(request.getRecipientBank());
        }
        if (request.getSenderBank() != null) {
            transaction.setSenderBank(request.getSenderBank());
        }
        if (request.getRecipientInn() != null) {
            transaction.setRecipientInn(request.getRecipientInn());
        }
        if (request.getCategory() != null) {
            transaction.setCategory(CategoryType.fromString(request.getCategory())); // ← Исправлено здесь!
        }
        if (request.getRecipientPhone() != null) {
            transaction.setRecipientPhone(request.getRecipientPhone());
        }

        // transaction.setLastModifiedBy(username); // если нужно

        transactionRepository.save(transaction);

        return new TransactionResponse(
                transaction.getId(),                          // 1
                transaction.getPersonType(),                  // 2
                transaction.getTransactionDate(),             // 3
                transaction.getTransactionType(),             // 4
                transaction.getComment(),                     // 5
                transaction.getAmount(),                      // 6
                transaction.getStatus(),                      // 7
                transaction.getSenderBank(),                  // 8
                transaction.getSenderAccount(),               // 9
                transaction.getRecipientBank(),               // 10
                transaction.getRecipientInn(),                // 11
                transaction.getRecipientAccount(),            // 12
                transaction.getCategory(),                    // 13
                transaction.getRecipientPhone(),              // 14
                transaction.getCreatedAt(),                   // 15 (если есть в сущности)
                transaction.getUpdatedAt()                    // 16 (если есть в сущности)

        );
    }

    @Override
    public List<TransactionResponse> filterTransactions(TransactionFilterRequest filterRequest, String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

        // Применяем фильтрацию "на памяти" по полям фильтра
        return transactions.stream()
                .filter(tx -> filterRequest.personType() == null ||
                        (tx.getPersonType() != null && tx.getPersonType() == filterRequest.personType()))
                .filter(tx -> filterRequest.transactionType() == null ||
                        tx.getTransactionType() == filterRequest.transactionType())
                .filter(tx -> filterRequest.status() == null ||
                        tx.getStatus() == filterRequest.status())
                .filter(tx -> filterRequest.senderBank() == null ||
                        (tx.getSenderBank() != null && tx.getSenderBank().equalsIgnoreCase(filterRequest.senderBank())))
                .filter(tx -> filterRequest.senderAccount() == null ||
                        (tx.getSenderAccount() != null && tx.getSenderAccount().equalsIgnoreCase(filterRequest.senderAccount())))
                .filter(tx -> filterRequest.recipientBank() == null ||
                        (tx.getRecipientBank() != null && tx.getRecipientBank().equalsIgnoreCase(filterRequest.recipientBank())))
                .filter(tx -> filterRequest.recipientInn() == null ||
                        (tx.getRecipientInn() != null && tx.getRecipientInn().equalsIgnoreCase(filterRequest.recipientInn())))
                .filter(tx -> filterRequest.recipientAccount() == null ||
                        (tx.getRecipientAccount() != null && tx.getRecipientAccount().equalsIgnoreCase(filterRequest.recipientAccount())))
                .filter(tx -> filterRequest.category() == null ||
                        tx.getCategory() == filterRequest.category())
                .filter(tx -> filterRequest.recipientPhone() == null ||
                        (tx.getRecipientPhone() != null && tx.getRecipientPhone().equalsIgnoreCase(filterRequest.recipientPhone())))
                .filter(tx -> filterRequest.comment() == null ||
                        (tx.getComment() != null && tx.getComment().toLowerCase().contains(filterRequest.comment().toLowerCase())))
                .filter(tx -> filterRequest.amountFrom() == null ||
                        (tx.getAmount() != null && tx.getAmount().compareTo(filterRequest.amountFrom()) >= 0))
                .filter(tx -> filterRequest.amountTo() == null ||
                        (tx.getAmount() != null && tx.getAmount().compareTo(filterRequest.amountTo()) <= 0))
                .filter(tx -> filterRequest.dateFrom() == null ||
                        (tx.getTransactionDate() != null && !tx.getTransactionDate().isBefore(filterRequest.dateFrom())))
                .filter(tx -> filterRequest.dateTo() == null ||
                        (tx.getTransactionDate() != null && !tx.getTransactionDate().isAfter(filterRequest.dateTo())))
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<Transaction> getTransactionsByFilter(TransactionFilterRequest filter, String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

        return transactions.stream()
                .filter(tx -> filter.personType() == null ||
                        (tx.getPersonType() != null && tx.getPersonType() == filter.personType()))
                .filter(tx -> filter.transactionType() == null ||
                        tx.getTransactionType() == filter.transactionType())
                .filter(tx -> filter.status() == null ||
                        tx.getStatus() == filter.status())
                .filter(tx -> filter.senderBank() == null ||
                        (tx.getSenderBank() != null && tx.getSenderBank().equalsIgnoreCase(filter.senderBank())))
                .filter(tx -> filter.senderAccount() == null ||
                        (tx.getSenderAccount() != null && tx.getSenderAccount().equalsIgnoreCase(filter.senderAccount())))
                .filter(tx -> filter.recipientBank() == null ||
                        (tx.getRecipientBank() != null && tx.getRecipientBank().equalsIgnoreCase(filter.recipientBank())))
                .filter(tx -> filter.recipientInn() == null ||
                        (tx.getRecipientInn() != null && tx.getRecipientInn().equalsIgnoreCase(filter.recipientInn())))
                .filter(tx -> filter.recipientAccount() == null ||
                        (tx.getRecipientAccount() != null && tx.getRecipientAccount().equalsIgnoreCase(filter.recipientAccount())))
                .filter(tx -> filter.category() == null ||
                        tx.getCategory() == filter.category())
                .filter(tx -> filter.recipientPhone() == null ||
                        (tx.getRecipientPhone() != null && tx.getRecipientPhone().equalsIgnoreCase(filter.recipientPhone())))
                .filter(tx -> filter.comment() == null ||
                        (tx.getComment() != null && tx.getComment().toLowerCase().contains(filter.comment().toLowerCase())))
                .filter(tx -> filter.amountFrom() == null ||
                        (tx.getAmount() != null && tx.getAmount().compareTo(filter.amountFrom()) >= 0))
                .filter(tx -> filter.amountTo() == null ||
                        (tx.getAmount() != null && tx.getAmount().compareTo(filter.amountTo()) <= 0))
                .filter(tx -> filter.dateFrom() == null ||
                        (tx.getTransactionDate() != null && !tx.getTransactionDate().isBefore(filter.dateFrom())))
                .filter(tx -> filter.dateTo() == null ||
                        (tx.getTransactionDate() != null && !tx.getTransactionDate().isAfter(filter.dateTo())))
                .collect(Collectors.toList());
    }
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Транзакция не найдена"));

        // Здесь проверяй и обновляй ТОЛЬКО те поля, которые не null!
        if (request.getPersonType() != null) {
            transaction.setPersonType(PersonType.valueOf(request.getPersonType()));
        }
        if (request.getTransactionDate() != null) {
            transaction.setTransactionDate(request.getTransactionDate());
        }
        if (request.getComment() != null) {
            transaction.setComment(request.getComment());
        }
        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }
        if (request.getStatus() != null) {
            transaction.setStatus(TransactionStatus.valueOf(request.getStatus()));
        }
        if (request.getSenderBank() != null) {
            transaction.setSenderBank(request.getSenderBank());
        }
        if (request.getRecipientBank() != null) {
            transaction.setRecipientBank(request.getRecipientBank());
        }
        if (request.getRecipientInn() != null) {
            transaction.setRecipientInn(request.getRecipientInn());
        }
        if (request.getCategory() != null) {
            transaction.setCategory(CategoryType.valueOf(request.getCategory()));
        }
        if (request.getRecipientPhone() != null) {
            transaction.setRecipientPhone(request.getRecipientPhone());
        }

        // Сохраняем изменения
        transactionRepository.save(transaction);

        // Возвращаем DTO-ответ
        return mapToTransactionResponse(transaction); // адаптируй под свой проект
    }
    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }

        if (!canEditTransaction(transaction, username)) {
            throw new IllegalStateException("Transaction with status " + transaction.getStatus() + " cannot be edited");
        }

        Transaction oldTransaction = copyTransaction(transaction);

        // Обновляем поля существующей транзакции
        transaction.setPersonType(request.getPersonType());

        transaction.setTransactionType(request.getTransactionType());
        transaction.setAmount(request.getAmount());
        transaction.setStatus(request.getStatus());
        transaction.setComment(request.getComment());
        transaction.setSenderBank(request.getSenderBank());
        transaction.setSenderAccount(request.getSenderAccount());
        transaction.setRecipientBank(request.getRecipientBank());
        transaction.setRecipientInn(request.getRecipientInn());
        transaction.setRecipientAccount(request.getRecipientAccount());
        transaction.setCategory(request.getCategory());
        transaction.setRecipientPhone(request.getRecipientPhone());

        // Сохраняем обновленную транзакцию
        transaction = transactionRepository.save(transaction);

        // Логирование изменений
        logTransactionChange(transaction, oldTransaction, transaction, user.getId(), "UPDATE");

        // Преобразуем сущность в DTO
        return mapToTransactionResponse(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id, String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }

        if (!canDeleteTransaction(transaction, username)) {
            throw new IllegalStateException("Transaction with status " + transaction.getStatus() + " cannot be deleted");
        }

        Transaction oldTransaction = copyTransaction(transaction);

        // Не удаляем физически, а меняем статус
        transaction.setStatus(TransactionStatus.DELETED);
        transaction = transactionRepository.save(transaction);

        // Логирование
        logTransactionChange(transaction, oldTransaction, transaction, user.getId(), "DELETE");
    }

    @Override
    public boolean canEditTransaction(Transaction transaction, String username) {
        return transaction.getUser().getUsername().equals(username)
                && switch (transaction.getStatus()) {
            case NEW -> true;
            default -> false;
        };
    }

    @Override
    public boolean canDeleteTransaction(Transaction transaction, String username) {
        return transaction.getUser().getUsername().equals(username)
                && switch (transaction.getStatus()) {
            case NEW -> true;
            default -> false;
        };
    }

    private void logTransactionChange(Transaction transaction, Transaction oldValue, Transaction newValue, Long userId, String action) {
        try {
            IntegrationLog log = new IntegrationLog();
            log.setEntityType("Transaction");
            log.setEntityId(transaction.getId());
            log.setAction(action);

            if (oldValue != null) {
                log.setOldValue(objectMapper.writeValueAsString(oldValue));
            }

            if (newValue != null) {
                log.setNewValue(objectMapper.writeValueAsString(newValue));
            }

            log.setUserId(userId);
            log.setTimestamp(LocalDateTime.now());

            integrationLogRepository.save(log);
        } catch (Exception e) {
            // Логгирование ошибки
            e.printStackTrace();
        }
    }

    private Transaction copyTransaction(Transaction original) {
        Transaction copy = new Transaction();
        copy.setId(original.getId());
        copy.setUser(original.getUser());
        copy.setPersonType(original.getPersonType());
        copy.setTransactionDate(original.getTransactionDate());
        copy.setTransactionType(original.getTransactionType());
        copy.setComment(original.getComment());
        copy.setAmount(original.getAmount());
        copy.setStatus(original.getStatus());
        copy.setSenderBank(original.getSenderBank());
        copy.setSenderAccount(original.getSenderAccount());
        copy.setRecipientBank(original.getRecipientBank());
        copy.setRecipientInn(original.getRecipientInn());
        copy.setRecipientAccount(original.getRecipientAccount());
        copy.setCategory(original.getCategory());
        copy.setRecipientPhone(original.getRecipientPhone());
        copy.setCreatedAt(original.getCreatedAt());
        copy.setUpdatedAt(original.getUpdatedAt());
        return copy;
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getPersonType(),
                transaction.getTransactionDate(),
                transaction.getTransactionType(),
                transaction.getComment(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getSenderBank(),
                transaction.getSenderAccount(),
                transaction.getRecipientBank(),
                transaction.getRecipientInn(),
                transaction.getRecipientAccount(),
                transaction.getCategory(),
                transaction.getRecipientPhone(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    // Если нужен второй вариант маппера, делай отдельным методом:
    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getPersonType(),
                transaction.getTransactionDate(),
                transaction.getTransactionType(),
                transaction.getComment(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getSenderBank(),
                transaction.getSenderAccount(),
                transaction.getRecipientBank(),
                transaction.getRecipientInn(),
                transaction.getRecipientAccount(),
                transaction.getCategory(),
                transaction.getRecipientPhone(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );

    }
}