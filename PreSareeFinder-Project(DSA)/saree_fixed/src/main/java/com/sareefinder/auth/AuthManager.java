package com.sareefinder.auth;

import com.sareefinder.ds.hashtable.UserHashTableOpenAddressing;
import com.sareefinder.model.User;
import com.sareefinder.model.Admin;

/**
 * AUTHENTICATION MANAGER — CO4
 * ==============================
 * Manages login for both User and Admin roles.
 * Uses the Open Addressing hash table for O(1) average lookup.
 *
 * CO4: Java Collections + custom hash table used together.
 * CO6: Auth is the entry point that connects to every other module.
 *
 * LOGIN LOGIC:
 *   1. Hash the username to find the bucket
 *   2. Linear probe if collision
 *   3. Compare password
 *   4. Return role-specific session object
 *
 * BIG-O: authenticate() → O(1) average, O(n) worst (all keys collide)
 */
public class AuthManager {

    private UserHashTableOpenAddressing authTable;

    // Seeded store names — must match data.js stores exactly
    public static final String STORE_KALANIKETAN  = "Kalaniketan";
    public static final String STORE_CHENNAI      = "Chennai Shopping Mall";
    public static final String STORE_SOUTH_INDIA  = "South India Shopping Mall";
    public static final String STORE_SHE_NEEDS    = "She Needs";
    public static final String STORE_MANYAVAR     = "Manyavar";
    public static final String STORE_MANGALYA     = "Mangalya";

    public AuthManager() {
        authTable = new UserHashTableOpenAddressing();
        seedAccounts();
    }

    // ─── SEED DEFAULT ACCOUNTS ────────────────────────────────────

    private void seedAccounts() {
        // ── Demo customers ──
        authTable.insert("user1",  "demo123", "Priya Sharma",  "+91 98765 43210", "user", null);
        authTable.insert("anita",  "demo123", "Anita Reddy",   "+91 91234 56789", "user", null);
        authTable.insert("kavya",  "user123", "Kavya Nair",    "+91 99887 65432", "user", null);
        authTable.insert("sunita", "pass123", "Sunita Verma",  "+91 97654 32109", "user", null);

        // ── Store admins ──
        authTable.insert("admin_kalaniketan", "admin123", "Ravi Kumar",    null, "admin", STORE_KALANIKETAN);
        authTable.insert("admin_chennai",     "admin123", "Meena Iyer",    null, "admin", STORE_CHENNAI);
        authTable.insert("admin_southindia",  "admin123", "Suresh Varma",  null, "admin", STORE_SOUTH_INDIA);
        authTable.insert("admin_sheneeds",    "admin123", "Deepa Nair",    null, "admin", STORE_SHE_NEEDS);
        authTable.insert("admin_manyavar",    "admin123", "Arjun Reddy",   null, "admin", STORE_MANYAVAR);
        authTable.insert("admin_mangalya",    "admin123", "Preethi Singh", null, "admin", STORE_MANGALYA);
    }

    // ─── LOGIN ────────────────────────────────────────────────────

    /**
     * Authenticate a user login.
     * Returns User object on success, null on failure.
     * O(1) average using hash table.
     */
    public User loginUser(String username, String password) {
        if (!authTable.authenticate(username, password, "user")) return null;
        return new User(username, password,
                        authTable.getName(username), "");
    }

    /**
     * Authenticate an admin login.
     * Returns Admin object with their assigned store.
     */
    public Admin loginAdmin(String username, String password) {
        if (!authTable.authenticate(username, password, "admin")) return null;
        return new Admin(username, password,
                         authTable.getName(username),
                         authTable.getStore(username));
    }

    // ─── REGISTER NEW USER ────────────────────────────────────────

    /**
     * Register a new customer account.
     * Returns false if username already taken.
     */
    public boolean registerUser(String username, String password,
                                 String name, String phone) {
        if (authTable.search(username) != -1) return false; // already exists
        return authTable.insert(username, password, name, phone, "user", null);
    }

    // ─── DISPLAY STRUCTURE (for CO4 demo) ─────────────────────────

    public void displayHashTableStructure() {
        authTable.displayStructure();
    }

    // ─── PRINT ALL LOGIN CREDENTIALS (demo only) ──────────────────

    public void printCredentials() {
        System.out.println("\n  ╔══ DEMO LOGIN CREDENTIALS ═══════════════════════════╗");
        System.out.println("  ║ CUSTOMERS:                                           ║");
        System.out.println("  ║   username: user1       password: demo123            ║");
        System.out.println("  ║   username: anita       password: demo123            ║");
        System.out.println("  ║   username: kavya       password: user123            ║");
        System.out.println("  ║                                                      ║");
        System.out.println("  ║ ADMINS (all password: admin123):                     ║");
        System.out.println("  ║   admin_kalaniketan  → Kalaniketan                   ║");
        System.out.println("  ║   admin_chennai      → Chennai Shopping Mall         ║");
        System.out.println("  ║   admin_southindia   → South India Shopping Mall     ║");
        System.out.println("  ║   admin_sheneeds     → She Needs                     ║");
        System.out.println("  ║   admin_manyavar     → Manyavar                      ║");
        System.out.println("  ║   admin_mangalya     → Mangalya                      ║");
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
    }
}
