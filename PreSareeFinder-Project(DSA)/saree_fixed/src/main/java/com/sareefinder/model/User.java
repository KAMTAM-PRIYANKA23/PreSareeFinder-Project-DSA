package com.sareefinder.model;

/**
 * USER MODEL
 * ==========
 * Represents a customer account stored in the user hash table.
 * CO4: Used as the value in our HashMap<String, User>.
 */
public class User {
    private String username;
    private String password;
    private String name;
    private String phone;

    public User(String username, String password, String name, String phone) {
        this.username = username;
        this.password = password;
        this.name     = name;
        this.phone    = phone;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName()     { return name; }
    public String getPhone()    { return phone; }

    public boolean checkPassword(String raw) { return this.password.equals(raw); }

    @Override
    public String toString() {
        return String.format("User[%s | %s | %s]", username, name, phone);
    }
}
