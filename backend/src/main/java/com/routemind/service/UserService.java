package com.routemind.service;

import org.springframework.stereotype.Service;
import com.routemind.controller.errors.ConflictError;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.entity.User;
import com.routemind.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User createIfNotPresent(String username) {
        if (repo.findUserByUsernameIgnoreCase(username).isPresent()) {
            throw new ConflictError("User with username " + username + " already exists!");
        }

        User user = new User(username);
        repo.save(user);
        return user;
    }

    public User findById(Long id) {
        return repo.findUserByUserId(id).orElseThrow(
                () -> new NotFoundError("User with id " + id + " does not exist!")
        );
    }

    public User findByUsername(String username) {
        return repo.findUserByUsernameIgnoreCase(username).orElseThrow(
                () -> new NotFoundError("User with username " + username + " does not exist!")
        );
    }
}
