package com.example.kata.service;

import com.example.kata.entity.User;
import com.example.kata.dto.UserRequest;
import com.example.kata.dto.UserResponse;

import java.util.Optional;

/**
 * Интерфейс для работы с пользователями.
 */
public interface UserService {

    /**
     * Поиск пользователя по имени пользователя.
     * @param username имя пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Сохранение пользователя.
     * @param user объект User
     * @return сохраненный User
     */
    Optional<User> findById(Long id);
    Optional<User> updateUser(Long id, UserRequest userRequest);
    void deleteUser(Long id);
    User save(User user);

    /**
     * Проверка, существует ли пользователь с таким именем.
     * @param username имя пользователя
     * @return true, если существует
     */
    boolean existsByUsername(String username);

    /**
     * Проверка, существует ли пользователь с таким email.
     * @param email email
     * @return true, если существует
     */
    boolean existsByEmail(String email);

    /**
     * Создание нового пользователя из UserRequest и возврат DTO UserResponse.
     * @param userRequest данные для создания пользователя
     * @return DTO с информацией о созданном пользователе
     */
    UserResponse createUser(UserRequest userRequest);
}