package org.example.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private double money;
    private Role role;
    private Status status;

    private String provider;

    public User() {
        this.role = Role.USER;
        this.status = Status.ACTIVE;
        this.provider = "LOCAL";
    }

    public User(String username, String password, String email) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
    }


    public User(String username, String password, String email, Role role, String provider) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.status = Status.ACTIVE;
    }
}