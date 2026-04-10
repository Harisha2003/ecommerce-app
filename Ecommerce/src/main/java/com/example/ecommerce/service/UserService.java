package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAll() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        log.info("Total users found: {}", users.size());
        return users;
    }

    public User getById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        log.info("User found: {}", user.getUsername());
        return user;
    }

    public User create(User user) {
        log.info("Creating user: {}", user.getUsername());
        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());
        return saved;
    }

    public User update(Long id, User updated) {
        log.info("Updating user with id: {}", id);
        User user = getById(id);
        user.setUsername(updated.getUsername());
        user.setEmail(updated.getEmail());
        user.setPhone(updated.getPhone());
        user.setAddress(updated.getAddress());
        User saved = userRepository.save(user);
        log.info("User updated: {}", saved.getUsername());
        return saved;
    }

    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User not found with id: {}", id);
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }
}
