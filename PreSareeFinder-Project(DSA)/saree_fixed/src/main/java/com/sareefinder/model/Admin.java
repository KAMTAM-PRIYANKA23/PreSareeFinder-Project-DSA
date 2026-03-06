package com.sareefinder.model;

/**
 * ADMIN MODEL
 * ===========
 * Represents a store-manager (salesman) account.
 * Each admin is tied to exactly one store.
 * CO4: Stored in a separate HashMap<String, Admin>.
 */
public class Admin {
    private String username;
    private String password;
    private String name;
    private String store;   // The specific mall this admin manages

    public Admin(String username, String password, String name, String store) {
        this.username = username;
        this.password = password;
        this.name     = name;
        this.store    = store;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName()     { return name; }
    public String getStore()    { return store; }

    public boolean checkPassword(String raw) { return this.password.equals(raw); }

    @Override
    public String toString() {
        return String.format("Admin[%s | %s | Store: %s]", username, name, store);
    }
}
