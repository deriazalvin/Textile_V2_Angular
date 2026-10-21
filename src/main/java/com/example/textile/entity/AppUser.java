package com.example.textile.entity;

import com.example.textile.security.AppRole;
import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(nullable = false, length = 120)
    private String password;

    @Column(length = 120)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppRole role = AppRole.ROLE_USER;

    @Column(nullable = false)
    private boolean enabled = true;

    protected AppUser() {
    }

    public AppUser(String username, String password, String displayName, AppRole role) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
        this.enabled = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public AppRole getRole() {
        return role;
    }

    public void setRole(AppRole role) {
        this.role = role;
    }

    /** Classe CSS du badge d'etat du compte. */
    public String getEnabledBadgeClass() {
        return enabled ? "badge--success" : "badge--muted";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}