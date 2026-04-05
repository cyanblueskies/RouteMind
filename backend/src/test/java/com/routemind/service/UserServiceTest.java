package com.routemind.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.entity.User;
import com.routemind.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository repo;
    private UserService service;

    @BeforeEach
    void setUp() {
        repo = mock(UserRepository.class);
        service = new UserService(repo);
    }

    @Test
    void createSavesUserAndReturnsId() {
        User saved = new User("alice");
        saved.setUserId(1L);
        when(repo.save(any(User.class))).thenReturn(saved);

        Long id = service.createIfNotPresent("alice").getUserId();
        verify(repo).save(any(User.class));
    }

    @Test
    void findByIdReturnsUserWhenExists() {
        User user = new User("bob");
        user.setUserId(2L);
        when(repo.findUserByUserId(2L)).thenReturn(Optional.of(user));

         try {
             User result = service.findById(2L);
             assertEquals("bob", result.getUsername());
         } catch (NotFoundError e) {
             fail(e.getMessage());
        }

    }

    @Test
    void findByIdReturnsEmptyWhenNotExists() {
        when(repo.findUserByUserId(99L)).thenReturn(Optional.empty());

        NotFoundError ex = assertThrows(NotFoundError.class, () -> {
            User result = service.findById(99L);
        });

        assertEquals("User with id " + 99L + " does not exist!", ex.getMessage());
    }

    @Test
    void findByUsernameIgnoresCase() {
        String username = "Charlie";

        User user = new User(username);
        when(repo.findUserByUsernameIgnoreCase(username)).thenReturn(Optional.of(user));

        try {
            User result = service.findByUsername(username);
            assertEquals(username, result.getUsername());
        } catch (NotFoundError e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findByUsernameReturnsEmptyWhenNotExists() {
        String username = "nobody";

        when(repo.findUserByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

        NotFoundError ex = assertThrows(NotFoundError.class, () -> {
            User result = service.findByUsername(username);
        });

        assertEquals("User with username " + username + " does not exist!", ex.getMessage());
    }
}
