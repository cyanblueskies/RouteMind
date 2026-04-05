package com.routemind.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.controller.errors.UnauthorisedError;
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
    void createUserHashesPasswordAndSaves() {
        when(repo.findUserByUsernameIgnoreCase("alice")).thenReturn(Optional.empty());
        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setUserId(1L);
            return u;
        });

        User result = service.createUser("alice", "password123");
        verify(repo).save(any(User.class));
        assertNotEquals("password123", result.getPassword()); // should be hashed
        assertTrue(result.getPassword().startsWith("$2a$")); // bcrypt prefix
    }

    @Test
    void authenticateSucceedsWithCorrectPassword() {
        // Create a user with a known bcrypt hash
        User user = service.createUser("bob", "correct");
        when(repo.findUserByUsernameIgnoreCase("bob")).thenReturn(Optional.of(user));
        when(repo.save(any(User.class))).thenReturn(user);

        User result = service.authenticate("bob", "correct");
        assertEquals("bob", result.getUsername());
    }

    @Test
    void authenticateFailsWithWrongPassword() {
        when(repo.findUserByUsernameIgnoreCase("bob")).thenReturn(Optional.empty());
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User user = service.createUser("bob", "correct");
        when(repo.findUserByUsernameIgnoreCase("bob")).thenReturn(Optional.of(user));

        assertThrows(UnauthorisedError.class, () -> service.authenticate("bob", "wrong"));
    }

    @Test
    void findByIdReturnsUserWhenExists() {
        User user = new User("bob", "hashed");
        user.setUserId(2L);
        when(repo.findUserByUserId(2L)).thenReturn(Optional.of(user));

        User result = service.findById(2L);
        assertEquals("bob", result.getUsername());
    }

    @Test
    void findByIdThrowsWhenNotExists() {
        when(repo.findUserByUserId(99L)).thenReturn(Optional.empty());

        NotFoundError ex = assertThrows(NotFoundError.class, () -> service.findById(99L));
        assertEquals("User with id 99 does not exist!", ex.getMessage());
    }

    @Test
    void findByUsernameIgnoresCase() {
        User user = new User("Charlie", "hashed");
        when(repo.findUserByUsernameIgnoreCase("Charlie")).thenReturn(Optional.of(user));

        User result = service.findByUsername("Charlie");
        assertEquals("Charlie", result.getUsername());
    }

    @Test
    void findByUsernameThrowsWhenNotExists() {
        when(repo.findUserByUsernameIgnoreCase("nobody")).thenReturn(Optional.empty());

        NotFoundError ex = assertThrows(NotFoundError.class, () -> service.findByUsername("nobody"));
        assertEquals("User with username nobody does not exist!", ex.getMessage());
    }
}
