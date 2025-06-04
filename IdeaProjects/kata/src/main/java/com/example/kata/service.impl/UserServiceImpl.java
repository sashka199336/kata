package com.example.kata.service.impl;

import com.example.kata.dto.UserRequest;
import com.example.kata.dto.UserResponse;
import com.example.kata.entity.User;
import com.example.kata.repository.UserRepository;
import com.example.kata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> updateUser(Long id, UserRequest userRequest) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userRequest.getUsername());
            user.setEmail(userRequest.getEmail());
            user.setFirstName(userRequest.getFirstName());
            user.setLastName(userRequest.getLastName());
            // ВАЖНО: шифруем пароль!
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            userRepository.save(user);
            return user;
        });
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        // Можно добавить проверки на существование пользователя/почты
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        // ВАЖНО: шифруем пароль!
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }
}