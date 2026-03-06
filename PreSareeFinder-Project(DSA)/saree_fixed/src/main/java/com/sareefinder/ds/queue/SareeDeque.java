package com.sareefinder.ds.queue;

import com.sareefinder.model.Saree;

/**
 * DEQUE — Double Ended Queue — CO3
 * ==================================
 * Supports insert and delete from both front and rear.
 *
 * Used in this system for:
 *   • Admin "Undo last delete" — add back to front
 *   • Quick add to front of queue (VIP/urgent orders)
 *   • Browsing history — add to rear, remove from front (BFS style)
 *
 * BIG-O: All operations O(1)
 * Space: O(n)
 *
 * CO3 Requirement: Deque for flexible operations.
 */
public class SareeDeque {

    private static final int CAPACITY = 40;
    private Saree[] deque;
    private int     front, rear, count;

    public SareeDeque() {
        deque = new Saree[CAPACITY];
        front = CAPACITY / 2;
        rear  = CAPACITY / 2 - 1;
        count = 0;
    }

    // ─── INSERT FRONT — O(1) ──────────────────────────────────────
    public boolean insertFront(Saree s) {
        if (isFull()) return false;
        front        = (front - 1 + CAPACITY) % CAPACITY;
        deque[front] = s;
        count++;
        return true;
    }

    // ─── INSERT REAR — O(1) ───────────────────────────────────────
    public boolean insertRear(Saree s) {
        if (isFull()) return false;
        rear        = (rear + 1) % CAPACITY;
        deque[rear] = s;
        count++;
        return true;
    }

    // ─── DELETE FRONT — O(1) ──────────────────────────────────────
    public Saree deleteFront() {
        if (isEmpty()) return null;
        Saree s     = deque[front];
        deque[front] = null;
        front        = (front + 1) % CAPACITY;
        count--;
        return s;
    }

    // ─── DELETE REAR — O(1) ───────────────────────────────────────
    public Saree deleteRear() {
        if (isEmpty()) return null;
        Saree s    = deque[rear];
        deque[rear] = null;
        rear        = (rear - 1 + CAPACITY) % CAPACITY;
        count--;
        return s;
    }

    // ─── PEEK ─────────────────────────────────────────────────────
    public Saree peekFront() { return isEmpty() ? null : deque[front]; }
    public Saree peekRear()  { return isEmpty() ? null : deque[rear];  }

    // ─── DISPLAY — O(n) ───────────────────────────────────────────
    public void display() {
        if (isEmpty()) { System.out.println("  (deque empty)"); return; }
        System.out.println("  Deque contents [front → rear]:");
        int pos = front;
        int cnt = count;
        int i   = 1;
        while (cnt-- > 0) {
            System.out.println("    " + i++ + ". " + deque[pos].getName()
                + " | ₹" + (int) deque[pos].getPrice());
            pos = (pos + 1) % CAPACITY;
        }
    }

    public boolean isEmpty() { return count == 0; }
    public boolean isFull()  { return count == CAPACITY; }
    public int     size()    { return count; }
}
