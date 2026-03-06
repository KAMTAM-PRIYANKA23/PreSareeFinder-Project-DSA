package com.sareefinder.model;

/**
 * SAREE MODEL
 * ===========
 * Core data entity representing a saree in the system.
 * Used as the element type across ALL data structures:
 *   - Arrays, LinkedList nodes, HashTable values,
 *     Stack elements, Queue elements, Heap entries.
 *
 * CO2: This is the T (generic type) that all our ADTs store.
 */
public class Saree implements Comparable<Saree> {

    private int    id;
    private String name;
    private String type;        // Silk, Cotton, Designer, Wedding, Fancy, Traditional
    private String store;       // Shopping mall name
    private double price;       // Current sale price
    private double originalPrice;
    private int    discount;    // Percentage 0–90
    private boolean available;  // Stock status
    private String description;
    private int    popularity;  // Used by Priority Queue (CO3)

    // ─── Constructor ─────────────────────────────────────────────
    public Saree(int id, String name, String type, String store,
                 double originalPrice, int discount, boolean available,
                 String description) {
        this.id            = id;
        this.name          = name;
        this.type          = type;
        this.store         = store;
        this.originalPrice = originalPrice;
        this.discount      = discount;
        this.available     = available;
        this.description   = description;
        this.popularity    = 0;
        // Calculate discounted price
        this.price = discount > 0
            ? Math.round(originalPrice * (1.0 - discount / 100.0))
            : originalPrice;
    }

    // ─── Comparable: default sort by price (used by sorting algorithms) ──
    @Override
    public int compareTo(Saree other) {
        return Double.compare(this.price, other.price);
    }

    // ─── Getters & Setters ────────────────────────────────────────
    public int     getId()            { return id; }
    public String  getName()          { return name; }
    public String  getType()          { return type; }
    public String  getStore()         { return store; }
    public double  getPrice()         { return price; }
    public double  getOriginalPrice() { return originalPrice; }
    public int     getDiscount()      { return discount; }
    public boolean isAvailable()      { return available; }
    public String  getDescription()   { return description; }
    public int     getPopularity()    { return popularity; }

    public void setName(String name)              { this.name = name; }
    public void setAvailable(boolean available)   { this.available = available; }
    public void setPopularity(int p)              { this.popularity = p; }

    public void setDiscount(int discount) {
        this.discount = discount;
        this.price = discount > 0
            ? Math.round(originalPrice * (1.0 - discount / 100.0))
            : originalPrice;
    }

    public void setOriginalPrice(double price) {
        this.originalPrice = price;
        setDiscount(this.discount); // recalculate sale price
    }

    // ─── Display ──────────────────────────────────────────────────
    @Override
    public String toString() {
        String stockTag  = available ? "In Stock" : "OUT OF STOCK";
        String priceTag  = discount > 0
            ? String.format("₹%.0f  (was ₹%.0f, -%d%%)", price, originalPrice, discount)
            : String.format("₹%.0f", price);
        return String.format(
            "  [%2d] %-30s | %-12s | %-28s | %-30s | %s",
            id, name, type, store, priceTag, stockTag
        );
    }

    /** Short single-line format for cart/bill display */
    public String toShortString() {
        return String.format("%-30s | %-12s | %-28s | ₹%.0f",
            name, type, store, price);
    }
}
