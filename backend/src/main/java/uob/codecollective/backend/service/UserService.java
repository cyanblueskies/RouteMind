package uob.codecollective.backend.service;

import org.springframework.stereotype.Service;
import uob.codecollective.backend.controller.errors.ConflictError;
import uob.codecollective.backend.controller.errors.NotFoundError;
import uob.codecollective.backend.entity.User;
import uob.codecollective.backend.repository.UserRepository;

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
