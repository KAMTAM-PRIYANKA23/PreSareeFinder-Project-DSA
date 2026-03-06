package com.sareefinder.ds.heap;

import com.sareefinder.model.Saree;

/**
 * MAX-HEAP (by discount %) — CO3
 * ================================
 * A complete binary tree stored in an array where every parent
 * node has a value >= its children.
 *
 * Used in this system to:
 *   • Show TOP DISCOUNTED sarees — highest discount at root
 *   • Implement Priority Queue for "best deals" feature
 *
 * HEAP PROPERTY:
 *   Parent at index i  →  children at 2i+1 and 2i+2
 *   Child at index i   →  parent at (i-1)/2
 *
 * BIG-O ANALYSIS:
 * ┌──────────────────┬──────────┬────────────────────────────────┐
 * │ Operation        │ Time     │ Why                            │
 * ├──────────────────┼──────────┼────────────────────────────────┤
 * │ insert()         │ O(log n) │ sift-up at most height levels  │
 * │ extractMax()     │ O(log n) │ sift-down at most height levels│
 * │ peekMax()        │ O(1)     │ root is always max             │
 * │ buildHeap()      │ O(n)     │ Floyd's heapify — Θ(n) proven  │
 * │ heapSort()       │ O(n logn)│ n extracts, each O(log n)      │
 * └──────────────────┴──────────┴────────────────────────────────┘
 * Space: O(n) — stored in array, no pointer overhead.
 *
 * RECURRENCE RELATION for sift-down on tree of height h:
 *   T(h) = T(h-1) + O(1)  =>  T(h) = O(h) = O(log n)
 *
 * vs Priority Queue using sorted array:
 *   insert:     O(n) sorted array  vs  O(log n) heap  ← heap wins
 *   extractMax: O(1) sorted array  vs  O(log n) heap  ← draw
 *   For frequent inserts+extracts: heap is clearly better.
 *
 * CO3 Requirement: Heap + Priority Queue demonstration.
 */
public class MaxHeapDiscount {

    private Saree[] heap;
    private int     size;
    private int     capacity;

    public MaxHeapDiscount(int capacity) {
        this.capacity = capacity;
        this.heap     = new Saree[capacity];
        this.size     = 0;
    }

    // ─── Index helpers ────────────────────────────────────────────
    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i)   { return 2 * i + 1; }
    private int right(int i)  { return 2 * i + 2; }

    private int discountOf(int i) { return heap[i].getDiscount(); }

    // ─── INSERT — O(log n) ────────────────────────────────────────

    public boolean insert(Saree saree) {
        if (size == capacity) return false;
        heap[size] = saree;
        siftUp(size);
        size++;
        return true;
    }

    private void siftUp(int i) {
        while (i > 0 && discountOf(parent(i)) < discountOf(i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    // ─── EXTRACT MAX — O(log n) ───────────────────────────────────

    public Saree extractMax() {
        if (size == 0) return null;
        Saree max   = heap[0];
        heap[0]     = heap[--size];
        heap[size]  = null;
        siftDown(0);
        return max;
    }

    private void siftDown(int i) {
        int largest = i;
        int l = left(i), r = right(i);
        if (l < size && discountOf(l) > discountOf(largest)) largest = l;
        if (r < size && discountOf(r) > discountOf(largest)) largest = r;
        if (largest != i) {
            swap(i, largest);
            siftDown(largest);
        }
    }

    // ─── BUILD HEAP from array — O(n) ─────────────────────────────

    /**
     * Floyd's algorithm: heapify from last non-leaf upward.
     * Despite appearing O(n log n), the math proves it's O(n):
     *   sum_{i=0}^{log n} (n/2^i) * i  =  O(n)
     */
    public static MaxHeapDiscount buildHeap(Saree[] arr) {
        MaxHeapDiscount h = new MaxHeapDiscount(arr.length);
        System.arraycopy(arr, 0, h.heap, 0, arr.length);
        h.size = arr.length;
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            h.siftDown(i);
        }
        return h;
    }

    // ─── PEEK — O(1) ──────────────────────────────────────────────
    public Saree peekMax() { return size > 0 ? heap[0] : null; }

    // ─── TOP-K DISCOUNTED SAREES — O(k log n) ─────────────────────

    /**
     * Returns top k highest-discount sarees.
     * Used by the "Best Deals" feature.
     * Makes a copy so original heap is not destroyed.
     */
    public Saree[] topK(int k) {
        // Clone the heap
        MaxHeapDiscount copy = new MaxHeapDiscount(capacity);
        System.arraycopy(heap, 0, copy.heap, 0, size);
        copy.size = size;

        int     limit  = Math.min(k, size);
        Saree[] result = new Saree[limit];
        for (int i = 0; i < limit; i++) {
            result[i] = copy.extractMax();
        }
        return result;
    }

    // ─── DISPLAY ──────────────────────────────────────────────────

    public void displayTopDeals(int k) {
        if (size == 0) { System.out.println("  No sarees in discount heap."); return; }
        System.out.println("\n  ╔══ TOP " + k + " DISCOUNTED SAREES (Max-Heap) ══════════════╗");
        Saree[] top = topK(k);
        for (int i = 0; i < top.length; i++) {
            System.out.printf("  ║ %2d. %-28s | -%2d%% | ₹%-8.0f ║%n",
                i + 1, top[i].getName(), top[i].getDiscount(), top[i].getPrice());
        }
        System.out.println("  ╚══════════════════════════════════════════════════╝");
    }

    // ─── UTILITY ──────────────────────────────────────────────────
    private void swap(int i, int j) { Saree t = heap[i]; heap[i] = heap[j]; heap[j] = t; }

    public int     size()    { return size; }
    public boolean isEmpty() { return size == 0; }
}
