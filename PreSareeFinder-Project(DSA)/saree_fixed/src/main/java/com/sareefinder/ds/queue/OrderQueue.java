package com.sareefinder.ds.queue;

import com.sareefinder.model.Saree;

/**
 * CIRCULAR QUEUE (Array-based) — CO3
 * ====================================
 * Fixed-size queue where rear wraps around to front.
 * Used for ORDER PROCESSING — first order placed is first processed (FIFO).
 *
 * BIG-O ANALYSIS:
 * ┌─────────────┬──────────┐
 * │ Operation   │ Time     │
 * ├─────────────┼──────────┤
 * │ enqueue()   │  O(1)    │
 * │ dequeue()   │  O(1)    │
 * │ peek()      │  O(1)    │
 * │ isFull()    │  O(1)    │
 * │ isEmpty()   │  O(1)    │
 * └─────────────┴──────────┘
 * Space: O(n)
 *
 * vs Linear Queue:
 *   Circular queue reuses dequeued slots — no wasted space.
 *   Linear queue loses space after dequeue (front++ but never wraps).
 *
 * CO3 Requirement: Circular Queue for order processing.
 */
public class OrderQueue {

    private static final int CAPACITY = 30;
    private Saree[] queue;
    private int     front, rear, count;

    public OrderQueue() {
        queue = new Saree[CAPACITY];
        front = 0;
        rear  = -1;
        count = 0;
    }

    // ─── ENQUEUE — O(1) ───────────────────────────────────────────

    /** Add order to the back */
    public boolean enqueue(Saree saree) {
        if (isFull()) {
            System.out.println("  Order queue is full. Please wait.");
            return false;
        }
        rear         = (rear + 1) % CAPACITY;  // ← wrap-around
        queue[rear]  = saree;
        count++;
        return true;
    }

    // ─── DEQUEUE — O(1) ───────────────────────────────────────────

    /** Process next order (FIFO) */
    public Saree dequeue() {
        if (isEmpty()) {
            System.out.println("  No orders to process.");
            return null;
        }
        Saree s        = queue[front];
        queue[front]   = null;
        front          = (front + 1) % CAPACITY; // ← wrap-around
        count--;
        return s;
    }

    // ─── PEEK — O(1) ──────────────────────────────────────────────

    public Saree peek() {
        return isEmpty() ? null : queue[front];
    }

    // ─── DISPLAY — O(n) ───────────────────────────────────────────

    public void display() {
        if (isEmpty()) { System.out.println("  Order queue is empty."); return; }
        System.out.println("  ┌── Order Queue (FIFO — first placed, first processed) ─┐");
        int pos = front;
        int cnt = count;
        int i   = 1;
        while (cnt-- > 0) {
            System.out.printf("  │ %2d. %-55s│%n", i++,
                queue[pos].getName() + " | ₹" + (int) queue[pos].getPrice());
            pos = (pos + 1) % CAPACITY;
        }
        System.out.println("  └───────────────────────────────────────────────────────┘");
    }

    // ─── UTILITY ──────────────────────────────────────────────────

    public boolean isEmpty() { return count == 0; }
    public boolean isFull()  { return count == CAPACITY; }
    public int     size()    { return count; }
}
