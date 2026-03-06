package com.sareefinder.ds.hashtable;

import com.sareefinder.model.Saree;
import java.util.LinkedList;

/**
 * HASH TABLE — SEPARATE CHAINING — CO4
 * ======================================
 * Each bucket is a LinkedList (chain) of entries.
 * Collisions are resolved by appending to the chain.
 *
 * Used in this system for:
 *   • Fast saree lookup by ID  — O(1) average
 *   • Store-based filtering    — group sarees by store name
 *   • Category-based filtering — group sarees by type
 *
 * HASH FUNCTION:
 *   h(id) = id % tableSize
 *   (Division method — works well when tableSize is prime)
 *   We use tableSize = 31 (prime) to minimize clustering.
 *
 * BIG-O ANALYSIS:
 * ┌──────────────────┬───────────────┬──────────────┐
 * │ Operation        │ Average       │ Worst        │
 * ├──────────────────┼───────────────┼──────────────┤
 * │ insert()         │ O(1)          │ O(n)         │
 * │ search()         │ O(1)          │ O(n)         │
 * │ delete()         │ O(1)          │ O(n)         │
 * │ store filter     │ O(n/k)        │ O(n)         │
 * └──────────────────┴───────────────┴──────────────┘
 * Worst case when all keys hash to same bucket (all in one chain).
 * Load factor α = n/m.  We keep α < 0.75 for good performance.
 *
 * Space: O(n + m)  where m = number of buckets
 *
 * vs Open Addressing:
 *   Chaining handles load factors > 1 (chains grow unbounded).
 *   Open addressing fails when table is full.
 *   Chaining is simpler to implement delete.
 *
 * CO4 Requirement: Hash Table using chaining.
 */
public class SareeHashTableChaining {

    private static final int DEFAULT_SIZE = 31; // prime number

    // ─── Entry in each chain ──────────────────────────────────────
    private static class Entry {
        int   key;   // saree ID
        Saree value;
        Entry(int key, Saree value) { this.key = key; this.value = value; }
    }

    private LinkedList<Entry>[] table;
    private int size;        // number of entries
    private int tableSize;   // number of buckets

    @SuppressWarnings("unchecked")
    public SareeHashTableChaining(int tableSize) {
        this.tableSize = tableSize;
        this.table     = new LinkedList[tableSize];
        for (int i = 0; i < tableSize; i++) table[i] = new LinkedList<>();
        this.size = 0;
    }

    public SareeHashTableChaining() { this(DEFAULT_SIZE); }

    // ─── HASH FUNCTION ────────────────────────────────────────────

    /**
     * Division method: h(k) = k mod m
     * Works best when m is prime (reduces collisions).
     */
    private int hash(int key) {
        return Math.abs(key % tableSize);
    }

    // ─── INSERT — O(1) average ────────────────────────────────────

    public void insert(Saree saree) {
        int bucket = hash(saree.getId());
        // Check if ID already exists (update instead of duplicate)
        for (Entry e : table[bucket]) {
            if (e.key == saree.getId()) {
                e.value = saree; // update
                return;
            }
        }
        table[bucket].addFirst(saree.getId() < 0
            ? new Entry(saree.getId(), saree)
            : new Entry(saree.getId(), saree));
        size++;
    }

    // ─── SEARCH BY ID — O(1) average ──────────────────────────────

    public Saree searchById(int id) {
        int bucket = hash(id);
        for (Entry e : table[bucket]) {
            if (e.key == id) return e.value;
        }
        return null; // not found
    }

    // ─── DELETE — O(1) average ────────────────────────────────────

    public boolean delete(int id) {
        int bucket = hash(id);
        for (Entry e : table[bucket]) {
            if (e.key == id) {
                table[bucket].remove(e);
                size--;
                return true;
            }
        }
        return false;
    }

    // ─── UPDATE PRICE — O(1) average ──────────────────────────────

    public boolean updatePrice(int id, double newOriginalPrice) {
        Saree s = searchById(id);
        if (s == null) return false;
        s.setOriginalPrice(newOriginalPrice);
        return true;
    }

    // ─── UPDATE DISCOUNT — O(1) average ───────────────────────────

    public boolean updateDiscount(int id, int newDiscount) {
        Saree s = searchById(id);
        if (s == null) return false;
        s.setDiscount(newDiscount);
        return true;
    }

    // ─── UPDATE STOCK — O(1) average ──────────────────────────────

    public boolean updateStock(int id, boolean available) {
        Saree s = searchById(id);
        if (s == null) return false;
        s.setAvailable(available);
        return true;
    }

    // ─── FILTER BY STORE — O(n) ───────────────────────────────────

    /**
     * Returns all sarees from a given store.
     * Must scan all buckets — O(n) unavoidable for full-table scan.
     * Could be O(n/k) if sarees are uniformly distributed.
     */
    public Saree[] filterByStore(String storeName) {
        java.util.List<Saree> result = new java.util.ArrayList<>();
        for (LinkedList<Entry> bucket : table) {
            for (Entry e : bucket) {
                if (e.value.getStore().equalsIgnoreCase(storeName)) {
                    result.add(e.value);
                }
            }
        }
        return result.toArray(new Saree[0]);
    }

    // ─── FILTER BY TYPE — O(n) ────────────────────────────────────

    public Saree[] filterByType(String type) {
        java.util.List<Saree> result = new java.util.ArrayList<>();
        for (LinkedList<Entry> bucket : table) {
            for (Entry e : bucket) {
                if (e.value.getType().equalsIgnoreCase(type)) {
                    result.add(e.value);
                }
            }
        }
        return result.toArray(new Saree[0]);
    }

    // ─── FILTER BY PRICE RANGE — O(n) ─────────────────────────────

    public Saree[] filterByPriceRange(double min, double max) {
        java.util.List<Saree> result = new java.util.ArrayList<>();
        for (LinkedList<Entry> bucket : table) {
            for (Entry e : bucket) {
                if (e.value.getPrice() >= min && e.value.getPrice() <= max
                        && e.value.isAvailable()) {
                    result.add(e.value);
                }
            }
        }
        return result.toArray(new Saree[0]);
    }

    // ─── GET ALL — O(n) ───────────────────────────────────────────

    public Saree[] getAll() {
        java.util.List<Saree> result = new java.util.ArrayList<>();
        for (LinkedList<Entry> bucket : table) {
            for (Entry e : bucket) result.add(e.value);
        }
        return result.toArray(new Saree[0]);
    }

    // ─── DISPLAY TABLE STRUCTURE ──────────────────────────────────

    public void displayStructure() {
        System.out.println("\n  Hash Table Structure (Chaining) — tableSize=" + tableSize);
        for (int i = 0; i < tableSize; i++) {
            if (!table[i].isEmpty()) {
                System.out.print("  Bucket[" + String.format("%2d", i) + "]: ");
                for (Entry e : table[i]) {
                    System.out.print("[ID:" + e.key + " " + e.value.getName() + "] → ");
                }
                System.out.println("null");
            }
        }
        System.out.printf("  Load factor: %.2f (%d entries / %d buckets)%n",
            (double) size / tableSize, size, tableSize);
    }

    // ─── UTILITY ──────────────────────────────────────────────────

    public int     size()      { return size; }
    public boolean isEmpty()   { return size == 0; }
    public double  loadFactor(){ return (double) size / tableSize; }
}
