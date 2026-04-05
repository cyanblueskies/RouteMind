package uob.codecollective.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// TODO: actually implement this properly.
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;

    protected User() {}

    public User(String username) {
        this.username = username;
    }

    public String getUsername() { return this.username; }
    public Long getUserId() {   return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
