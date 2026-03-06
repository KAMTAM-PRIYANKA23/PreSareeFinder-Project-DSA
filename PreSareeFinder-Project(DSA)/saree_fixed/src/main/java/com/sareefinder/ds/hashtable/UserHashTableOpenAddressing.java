package com.sareefinder.ds.hashtable;

import com.sareefinder.model.Saree;

/**
 * HASH TABLE — OPEN ADDRESSING (Linear Probing) — CO4
 * ====================================================
 * All entries stored directly in the table array.
 * On collision, probe the next slot: h(k, i) = (h(k) + i) mod m
 *
 * Used in this system for:
 *   • User authentication table (username → User)
 *   • Admin session lookup (adminId → store)
 *   • Category → list of IDs index
 *
 * PROBING SEQUENCES:
 *   Linear:    h(k,i) = (h(k) + i)      mod m
 *   Quadratic: h(k,i) = (h(k) + i²)     mod m
 *   Double:    h(k,i) = (h1(k) + i*h2(k)) mod m
 *
 * We implement LINEAR PROBING for simplicity.
 *
 * BIG-O ANALYSIS (with load factor α = n/m):
 * ┌──────────────────┬───────────────────────┬──────────────────┐
 * │ Operation        │ Average (α < 0.5)     │ Worst            │
 * ├──────────────────┼───────────────────────┼──────────────────┤
 * │ insert()         │ O(1/(1-α)) ≈ O(1)    │ O(n)             │
 * │ search (hit)     │ O(1+α/2) ≈ O(1)      │ O(n)             │
 * │ search (miss)    │ O(1/(1-α)²)           │ O(n)             │
 * │ delete()         │ O(1) + rehash         │ O(n)             │
 * └──────────────────┴───────────────────────┴──────────────────┘
 *
 * ★ Key constraint: table must NEVER be completely full.
 *   We resize (rehash) when load factor exceeds 0.7.
 *
 * vs Chaining:
 *   Open addressing uses no extra memory (no linked list pointers).
 *   Better cache performance — all data in one contiguous array.
 *   Degrades fast when load factor is high.
 *
 * CO4 Requirement: Hash Table using open addressing.
 */
public class UserHashTableOpenAddressing {

    private static final int    DEFAULT_SIZE   = 17; // prime
    private static final double MAX_LOAD       = 0.7;
    private static final String DELETED_MARKER = "##DELETED##"; // tombstone

    private String[]   keys;    // username
    private String[]   values;  // password (hashed in real apps)
    private String[]   names;   // display name
    private String[]   phones;
    private String[]   roles;   // "user" or "admin"
    private String[]   stores;  // admin's store (null for users)
    private int        size;
    private int        tableSize;

    public UserHashTableOpenAddressing() {
        this(DEFAULT_SIZE);
    }

    public UserHashTableOpenAddressing(int tableSize) {
        this.tableSize = tableSize;
        this.keys      = new String[tableSize];
        this.values    = new String[tableSize];
        this.names     = new String[tableSize];
        this.phones    = new String[tableSize];
        this.roles     = new String[tableSize];
        this.stores    = new String[tableSize];
        this.size      = 0;
    }

    // ─── HASH FUNCTION ────────────────────────────────────────────

    /**
     * Polynomial rolling hash for string keys.
     * h(s) = (s[0]*p^0 + s[1]*p^1 + ... ) mod m
     * p=31 (prime), keeps distribution uniform.
     */
    private int hash(String key) {
        long hash  = 0;
        long prime = 31;
        long power = 1;
        for (char c : key.toCharArray()) {
            hash  = (hash + (c - 'a' + 1) * power) % tableSize;
            power = (power * prime) % tableSize;
        }
        return (int) Math.abs(hash);
    }

    // ─── LINEAR PROBE sequence ────────────────────────────────────

    private int probe(int h, int i) {
        return (h + i) % tableSize;
    }

    // ─── INSERT / REGISTER — O(1) average ─────────────────────────

    public boolean insert(String username, String password,
                          String name, String phone,
                          String role, String store) {
        if ((double) size / tableSize >= MAX_LOAD) rehash();

        int h = hash(username);
        for (int i = 0; i < tableSize; i++) {
            int idx = probe(h, i);
            if (keys[idx] == null || keys[idx].equals(DELETED_MARKER)) {
                keys[idx]   = username;
                values[idx] = password;
                names[idx]  = name;
                phones[idx] = phone;
                roles[idx]  = role;
                stores[idx] = store;
                size++;
                return true;
            }
            if (keys[idx].equals(username)) return false; // already exists
        }
        return false;
    }

    // ─── SEARCH / LOGIN — O(1) average ────────────────────────────

    /**
     * Returns index of the matching username, or -1.
     * Stops at null (never inserted) but continues past DELETED (tombstone).
     */
    public int search(String username) {
        int h = hash(username);
        for (int i = 0; i < tableSize; i++) {
            int idx = probe(h, i);
            if (keys[idx] == null) return -1; // empty slot = not found
            if (!keys[idx].equals(DELETED_MARKER)
                    && keys[idx].equals(username)) return idx;
        }
        return -1;
    }

    // ─── AUTHENTICATE — O(1) average ──────────────────────────────

    public boolean authenticate(String username, String password, String role) {
        int idx = search(username);
        if (idx == -1) return false;
        return values[idx].equals(password) && roles[idx].equals(role);
    }

    public String getName(String username) {
        int idx = search(username);
        return idx == -1 ? null : names[idx];
    }

    public String getStore(String username) {
        int idx = search(username);
        return idx == -1 ? null : stores[idx];
    }

    // ─── DELETE — O(1) average (tombstone method) ─────────────────

    /**
     * We use a tombstone (DELETED_MARKER) instead of null.
     * If we used null, search would stop early and miss entries
     * that were inserted after a delete at this position.
     */
    public boolean delete(String username) {
        int idx = search(username);
        if (idx == -1) return false;
        keys[idx]   = DELETED_MARKER; // tombstone
        values[idx] = null;
        size--;
        return true;
    }

    // ─── REHASH — O(n) ────────────────────────────────────────────

    /**
     * Double the table size and re-insert all entries.
     * Triggered when load factor > 0.7.
     * Amortized cost per insert: O(1).
     */
    private void rehash() {
        String[] oldKeys   = keys;
        String[] oldVals   = values;
        String[] oldNames  = names;
        String[] oldPhones = phones;
        String[] oldRoles  = roles;
        String[] oldStores = stores;
        int      oldSize   = tableSize;

        tableSize = nextPrime(tableSize * 2);
        keys      = new String[tableSize];
        values    = new String[tableSize];
        names     = new String[tableSize];
        phones    = new String[tableSize];
        roles     = new String[tableSize];
        stores    = new String[tableSize];
        size      = 0;

        for (int i = 0; i < oldSize; i++) {
            if (oldKeys[i] != null && !oldKeys[i].equals(DELETED_MARKER)) {
                insert(oldKeys[i], oldVals[i], oldNames[i],
                       oldPhones[i], oldRoles[i], oldStores[i]);
            }
        }
        System.out.println("  [HashTable] Rehashed to size " + tableSize);
    }

    // ─── DISPLAY STRUCTURE ────────────────────────────────────────

    public void displayStructure() {
        System.out.println("\n  Hash Table — Open Addressing (tableSize=" + tableSize + ")");
        for (int i = 0; i < tableSize; i++) {
            if (keys[i] != null && !keys[i].equals(DELETED_MARKER)) {
                System.out.printf("  [%2d] %s (%s, %s)%n", i, keys[i], names[i], roles[i]);
            }
        }
        System.out.printf("  Load factor: %.2f%n", (double) size / tableSize);
    }

    // ─── UTILITY ──────────────────────────────────────────────────

    public int     size()      { return size; }
    public double  loadFactor(){ return (double) size / tableSize; }

    private int nextPrime(int n) {
        while (!isPrime(n)) n++;
        return n;
    }
    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) if (n % i == 0) return false;
        return true;
    }
}
