package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Object id;
    private String username;
    private String password;
    private String email;
    private Role role;

    public User() {
        this.role = Role.USER;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String username, String password, String email, Role role) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}
