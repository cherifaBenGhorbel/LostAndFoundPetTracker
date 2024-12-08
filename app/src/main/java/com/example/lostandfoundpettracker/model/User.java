package com.example.lostandfoundpettracker.model;

public class User {
    public String username;
    public String email;
    public String phone;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String phone, String password) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

}
