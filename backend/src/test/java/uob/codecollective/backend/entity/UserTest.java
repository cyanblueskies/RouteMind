package uob.codecollective.backend.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructorSetsUsername() {
        User user = new User("alice");
        assertEquals("alice", user.getUsername());
    }

    @Test
    void setUserIdWorks() {
        User user = new User("bob");
        user.setUserId(42L);
        assertEquals(42L, user.getUserId());
    }

    @Test
    void newUserIdIsNullBeforePersist() {
        User user = new User("charlie");
        assertNull(user.getUserId());
    }
}
