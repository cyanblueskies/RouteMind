package com.routemind.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.routemind.controller.errors.ConflictError;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.controller.errors.UnauthorisedError;
import com.routemind.entity.User;
import com.routemind.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User createUser(String username, String rawPassword) {
        if (repo.findUserByUsernameIgnoreCase(username).isPresent()) {
            throw new ConflictError("User with username " + username + " already exists!");
        }
        User user = new User(username, passwordEncoder.encode(rawPassword));
        repo.save(user);
        return user;
    }

    public User authenticate(String username, String rawPassword) {
        User user = repo.findUserByUsernameIgnoreCase(username).orElseThrow(
                () -> new NotFoundError("User with username " + username + " does not exist!")
        );
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UnauthorisedError("Invalid password");
        }
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
