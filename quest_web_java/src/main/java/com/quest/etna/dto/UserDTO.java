package com.quest.etna.dto;

import com.quest.etna.model.User;
import com.quest.etna.model.UserRole;
import org.springframework.util.StringUtils;

import java.util.Set;

public class UserDTO {

    private int id;
    private String username;
    private String role;
    //private String password;

    public UserDTO() { }

    // Getters et setters

    public int getId() { return id;}

    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    }




