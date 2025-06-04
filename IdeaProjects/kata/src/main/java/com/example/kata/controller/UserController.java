package com.example.kata.controller;

import java.util.Optional;
import com.example.kata.entity.User;
import com.example.kata.dto.UserRequest;
import com.example.kata.dto.UserResponse;
import com.example.kata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.ok(response);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id); // Должен быть такой метод в сервисе!
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest) {

        Optional<User> updatedUser = userService.updateUser(id, userRequest);

        if (updatedUser.isPresent()) {
            User user = updatedUser.get();
            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Проверим, существует ли пользователь
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

    }
}