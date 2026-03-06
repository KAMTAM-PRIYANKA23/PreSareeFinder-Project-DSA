package com.sareefinder.ds.linkedlist;

import com.sareefinder.model.Saree;

/**
 * CIRCULAR LINKED LIST — CO2
 * ==========================
 * Last node's next points back to head instead of null.
 *
 * Used in this system for:
 *   • "Featured Sarees" carousel — cycle through featured items
 *   • Round-robin store display — rotate through all stores
 *
 * BIG-O: Same as Singly but useful when wrapping around is natural.
 *
 * CO2 Requirement: detect cycles — in a circular list a cycle is
 * intentional and hasCycle() should return true.
 */
public class CircularLinkedList {

    public static class CNode {
        public Saree data;
        public CNode next;
        public CNode(Saree data) { this.data = data; this.next = null; }
    }

    private CNode last;   // tail — last.next == head
    private int   size;

    public CircularLinkedList() { last = null; size = 0; }

    // ─── INSERT ───────────────────────────────────────────────────

    /** O(1) */
    public void insert(Saree saree) {
        CNode node = new CNode(saree);
        if (last == null) {
            last      = node;
            last.next = last; // point to itself
        } else {
            node.next = last.next; // new node points to head
            last.next = node;      // old tail points to new node
            last      = node;      // move tail to new node
        }
        size++;
    }

    // ─── DELETE ───────────────────────────────────────────────────

    /** O(n) */
    public boolean deleteById(int id) {
        if (last == null) return false;
        CNode head = last.next;

        // Only one node
        if (head == last && head.data.getId() == id) {
            last = null; size--; return true;
        }

        CNode prev = last;
        CNode cur  = head;
        do {
            if (cur.data.getId() == id) {
                prev.next = cur.next;
                if (cur == last) last = prev;
                size--;
                return true;
            }
            prev = cur;
            cur  = cur.next;
        } while (cur != head);
        return false;
    }

    // ─── TRAVERSE ─────────────────────────────────────────────────

    /** O(n) — stop when we reach head again */
    public void traverse() {
        if (last == null) { System.out.println("  (empty)"); return; }
        CNode cur = last.next; // head
        int   i   = 1;
        do {
            System.out.println("  [Featured " + i + "] " + cur.data.getName()
                + " | " + cur.data.getStore()
                + " | ₹" + (int) cur.data.getPrice());
            cur = cur.next;
            i++;
        } while (cur != last.next);
    }

    // ─── CYCLE DETECTION ──────────────────────────────────────────

    /**
     * In a circular list, hasCycle() always returns true.
     * This demonstrates Floyd's algorithm (same as SinglyLinkedList)
     * and proves the structure is intentionally circular — CO2.
     */
    public boolean hasCycle() {
        if (last == null) return false;
        CNode slow = last.next;
        CNode fast = last.next;
        do {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
            if (fast == last.next || fast.next == last.next) break;
        } while (slow != last.next);
        return last != null; // circular list always has a cycle
    }

    public int     size()    { return size; }
    public boolean isEmpty() { return last == null; }
}
