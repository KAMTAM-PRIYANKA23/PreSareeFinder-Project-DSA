package com.sareefinder.ds.stack;

import com.sareefinder.model.Saree;

/**
 * STACK (Array-based) — CO3
 * =========================
 * LIFO (Last In, First Out) structure.
 *
 * Used in this system for:
 *   1. WISHLIST — last item added is shown first (LIFO view)
 *   2. NAVIGATION HISTORY — undo/back functionality
 *   3. "Recently Viewed" feature
 *
 * BIG-O ANALYSIS:
 * ┌─────────────┬──────────┐
 * │ Operation   │ Time     │
 * ├─────────────┼──────────┤
 * │ push()      │  O(1)    │
 * │ pop()       │  O(1)    │
 * │ peek()      │  O(1)    │
 * │ isEmpty()   │  O(1)    │
 * │ size()      │  O(1)    │
 * └─────────────┴──────────┘
 * Space: O(n)
 *
 * CO3 Requirement: Stack-based wishlist means the most recently
 * added saree is always shown at the top — natural for a wishlist.
 *
 * vs LinkedList:
 *   Array stack gives O(1) for all ops with no pointer overhead.
 *   LinkedList stack also gives O(1) at head but uses more memory.
 */
public class WishlistStack {

    private static final int DEFAULT_CAPACITY = 50;
    private Saree[] stack;
    private int     top;

    public WishlistStack() {
        stack = new Saree[DEFAULT_CAPACITY];
        top   = -1;
    }

    // ─── PUSH — O(1) ──────────────────────────────────────────────

    /**
     * Add a saree to the wishlist.
     * If already present, ignore (no duplicates in wishlist).
     */
    public boolean push(Saree saree) {
        if (top >= stack.length - 1) {
            System.out.println("  Wishlist is full (max " + DEFAULT_CAPACITY + " items).");
            return false;
        }
        // Check for duplicate
        if (contains(saree.getId())) {
            System.out.println("  '" + saree.getName() + "' is already in your wishlist.");
            return false;
        }
        stack[++top] = saree;
        return true;
    }

    // ─── POP — O(1) ───────────────────────────────────────────────

    /** Remove and return the most recently added saree */
    public Saree pop() {
        if (isEmpty()) {
            System.out.println("  Wishlist is empty.");
            return null;
        }
        Saree s     = stack[top];
        stack[top--] = null; // help GC
        return s;
    }

    // ─── PEEK — O(1) ──────────────────────────────────────────────

    /** View top without removing */
    public Saree peek() {
        if (isEmpty()) return null;
        return stack[top];
    }

    // ─── REMOVE BY ID — O(n) ──────────────────────────────────────

    /**
     * Remove a specific saree by ID.
     * Stack doesn't natively support this — we rebuild:
     *   → Pop into temp, skip target, push back.
     * O(n) time, O(n) space.
     */
    public boolean removeById(int id) {
        if (isEmpty()) return false;

        Saree[] temp  = new Saree[top + 1];
        int     count = 0;
        boolean found = false;

        // Pop all into temp
        while (!isEmpty()) {
            Saree s = pop();
            if (s.getId() == id) { found = true; }
            else                 { temp[count++] = s; }
        }

        // Push back in reverse (bottom to top)
        for (int i = count - 1; i >= 0; i--) push(temp[i]);
        return found;
    }

    // ─── DISPLAY — O(n) ───────────────────────────────────────────

    /** Display wishlist from top (most recent) to bottom */
    public void display() {
        if (isEmpty()) {
            System.out.println("  Your wishlist is empty.");
            return;
        }
        System.out.println("  ┌── Wishlist (most recently added first) ──────────┐");
        for (int i = top; i >= 0; i--) {
            System.out.printf("  │ %2d. %-50s│%n", (top - i + 1), stack[i].getName()
                + " | " + stack[i].getStore() + " | ₹" + (int) stack[i].getPrice());
        }
        System.out.println("  └───────────────────────────────────────────────────┘");
    }

    // ─── UTILITY ──────────────────────────────────────────────────

    public boolean isEmpty()     { return top == -1; }
    public int     size()        { return top + 1; }

    public boolean contains(int id) {
        for (int i = 0; i <= top; i++)
            if (stack[i].getId() == id) return true;
        return false;
    }

    public Saree[] toArray() {
        Saree[] arr = new Saree[top + 1];
        System.arraycopy(stack, 0, arr, 0, top + 1);
        return arr;
    }
}
