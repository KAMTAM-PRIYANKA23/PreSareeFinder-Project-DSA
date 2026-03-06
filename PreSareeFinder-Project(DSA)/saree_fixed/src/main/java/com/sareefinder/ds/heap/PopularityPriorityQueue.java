package com.sareefinder.ds.heap;

import com.sareefinder.model.Saree;

/**
 * PRIORITY QUEUE — by Popularity Score — CO3
 * ===========================================
 * A Min-Heap by price (so cheapest sarees get priority for budget shoppers)
 * AND a Max-Heap wrapper by popularity (most popular shown first).
 *
 * We implement this as a MAX-HEAP by popularity score.
 *
 * Used in this system to:
 *   • Show most-popular sarees first in the main catalog
 *   • Budget filter: show cheapest available first (min-heap by price)
 *
 * BIG-O: insert O(log n), extractTop O(log n), peek O(1)
 *
 * CO3 Requirement: Priority Queue demonstration alongside Heap.
 *
 * Comparison with OrderQueue (CO3):
 *   OrderQueue:    FIFO — fair, order of arrival
 *   PriorityQueue: Priority-based — most popular/cheapest served first
 */
public class PopularityPriorityQueue {

    private Saree[] heap;
    private int     size;
    private int     capacity;

    public PopularityPriorityQueue(int capacity) {
        this.capacity = capacity;
        this.heap     = new Saree[capacity];
        this.size     = 0;
    }

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i)   { return 2 * i + 1; }
    private int right(int i)  { return 2 * i + 2; }

    // Higher popularity = higher priority
    private int priority(int i) { return heap[i].getPopularity(); }

    // ─── ENQUEUE — O(log n) ───────────────────────────────────────
    public boolean enqueue(Saree s) {
        if (size == capacity) return false;
        heap[size] = s;
        siftUp(size++);
        return true;
    }

    private void siftUp(int i) {
        while (i > 0 && priority(parent(i)) < priority(i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    // ─── DEQUEUE (highest priority) — O(log n) ───────────────────
    public Saree dequeue() {
        if (size == 0) return null;
        Saree top  = heap[0];
        heap[0]    = heap[--size];
        heap[size] = null;
        if (size > 0) siftDown(0);
        return top;
    }

    private void siftDown(int i) {
        int best = i, l = left(i), r = right(i);
        if (l < size && priority(l) > priority(best)) best = l;
        if (r < size && priority(r) > priority(best)) best = r;
        if (best != i) { swap(i, best); siftDown(best); }
    }

    // ─── PEEK — O(1) ──────────────────────────────────────────────
    public Saree peek() { return size > 0 ? heap[0] : null; }

    // ─── DISPLAY TOP N ────────────────────────────────────────────
    public void displayTopPopular(int n) {
        if (size == 0) { System.out.println("  No sarees in queue."); return; }
        // Clone to preserve heap
        PopularityPriorityQueue copy = new PopularityPriorityQueue(capacity);
        System.arraycopy(heap, 0, copy.heap, 0, size);
        copy.size = size;

        int limit = Math.min(n, size);
        System.out.println("\n  ╔══ TOP " + limit + " POPULAR SAREES (Priority Queue) ═══════╗");
        for (int i = 1; i <= limit; i++) {
            Saree s = copy.dequeue();
            System.out.printf("  ║ %2d. %-28s | Score:%-3d | ₹%-8.0f ║%n",
                i, s.getName(), s.getPopularity(), s.getPrice());
        }
        System.out.println("  ╚═════════════════════════════════════════════════╝");
    }

    private void swap(int i, int j) { Saree t = heap[i]; heap[i] = heap[j]; heap[j] = t; }

    public int     size()    { return size; }
    public boolean isEmpty() { return size == 0; }

    // ─── MIN-HEAP variant (by price) ──────────────────────────────

    /**
     * Static helper: returns the cheapest saree in an array.
     * Uses a min-heap built in O(n), then extract-min in O(log n).
     * Total: O(n) for build + O(log n) per extract.
     *
     * CO3: Demonstrates Priority Queue for budget filtering.
     */
    public static Saree[] cheapestFirst(Saree[] sarees) {
        // Build min-heap by price using standard Java PriorityQueue
        java.util.PriorityQueue<Saree> pq =
            new java.util.PriorityQueue<>((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
        for (Saree s : sarees) pq.offer(s);

        Saree[] result = new Saree[sarees.length];
        for (int i = 0; i < result.length; i++) result[i] = pq.poll();
        return result;
    }
}
