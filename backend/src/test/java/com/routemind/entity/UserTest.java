package com.routemind.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructorSetsUsernameAndPassword() {
        User user = new User("alice", "secret123");
        assertEquals("alice", user.getUsername());
        assertEquals("secret123", user.getPassword());
    }

    @Test
    void setUserIdWorks() {
        User user = new User("bob", "pass");
        user.setUserId(42L);
        assertEquals(42L, user.getUserId());
    }

    @Test
    void newUserIdIsNullBeforePersist() {
        User user = new User("charlie", "pass");
        assertNull(user.getUserId());
    }

    @Test
    void createdAtIsSetOnConstruction() {
        User user = new User("dave", "pass");
        assertNotNull(user.getCreatedAt());
    }
}
