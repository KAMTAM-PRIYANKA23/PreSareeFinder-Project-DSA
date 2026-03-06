package com.sareefinder.ds.linkedlist;

import com.sareefinder.model.Saree;

/**
 * SINGLY LINKED LIST — CO2
 * ========================
 * Generic singly-linked list of Saree objects.
 *
 * Used in this system for:
 *   • Wishlist management (CO5)
 *   • Hash table chaining buckets (CO4)
 *
 * BIG-O ANALYSIS:
 * ┌───────────────────────┬──────────┬───────────┐
 * │ Operation             │ Average  │ Worst     │
 * ├───────────────────────┼──────────┼───────────┤
 * │ insertAtHead()        │  O(1)    │  O(1)     │
 * │ insertAtTail()        │  O(n)    │  O(n)     │
 * │ deleteByValue()       │  O(n)    │  O(n)     │
 * │ search()              │  O(n)    │  O(n)     │
 * │ traverse()            │  O(n)    │  O(n)     │
 * │ reverse()             │  O(n)    │  O(n)     │
 * └───────────────────────┴──────────┴───────────┘
 *
 * Space Complexity: O(n)
 *
 * Trade-offs vs Array:
 *   ✓ Dynamic size — no reallocation needed
 *   ✓ O(1) insert at head
 *   ✗ No random access — must traverse
 *   ✗ Extra memory per node (pointer overhead)
 */
public class SinglyLinkedList {

    // ─── Node Inner Class ─────────────────────────────────────────
    public static class Node {
        public Saree data;
        public Node  next;

        public Node(Saree data) {
            this.data = data;
            this.next = null;
        }
    }

    // ─── Fields ───────────────────────────────────────────────────
    private Node head;
    private int  size;

    public SinglyLinkedList() {
        head = null;
        size = 0;
    }

    // ─── INSERT ───────────────────────────────────────────────────

    /** O(1) — insert at beginning */
    public void insertAtHead(Saree saree) {
        Node newNode = new Node(saree);
        newNode.next = head;
        head = newNode;
        size++;
    }

    /** O(n) — insert at end */
    public void insertAtTail(Saree saree) {
        Node newNode = new Node(saree);
        if (head == null) {
            head = newNode;
        } else {
            Node cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = newNode;
        }
        size++;
    }

    /** O(n) — insert after a given position (0-indexed) */
    public void insertAtPosition(int pos, Saree saree) {
        if (pos <= 0) { insertAtHead(saree); return; }
        Node cur = head;
        for (int i = 0; i < pos - 1 && cur != null; i++) cur = cur.next;
        if (cur == null) { insertAtTail(saree); return; }
        Node newNode  = new Node(saree);
        newNode.next  = cur.next;
        cur.next      = newNode;
        size++;
    }

    // ─── DELETE ───────────────────────────────────────────────────

    /** O(n) — delete by saree ID */
    public boolean deleteById(int id) {
        if (head == null) return false;

        // Special case: head is the target
        if (head.data.getId() == id) {
            head = head.next;
            size--;
            return true;
        }

        Node prev = head;
        Node cur  = head.next;
        while (cur != null) {
            if (cur.data.getId() == id) {
                prev.next = cur.next;
                size--;
                return true;
            }
            prev = cur;
            cur  = cur.next;
        }
        return false;
    }

    // ─── SEARCH ───────────────────────────────────────────────────

    /** O(n) — linear search by ID */
    public Saree searchById(int id) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.getId() == id) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    /** O(n) — search by name (case-insensitive substring) */
    public SinglyLinkedList searchByName(String keyword) {
        SinglyLinkedList result = new SinglyLinkedList();
        Node cur = head;
        String kw = keyword.toLowerCase();
        while (cur != null) {
            if (cur.data.getName().toLowerCase().contains(kw)) {
                result.insertAtTail(cur.data);
            }
            cur = cur.next;
        }
        return result;
    }

    // ─── TRAVERSE ─────────────────────────────────────────────────

    /** O(n) — print all nodes */
    public void traverse() {
        if (head == null) {
            System.out.println("  (empty list)");
            return;
        }
        Node cur = head;
        int  idx = 1;
        while (cur != null) {
            System.out.println("  " + idx + ". " + cur.data);
            cur = cur.next;
            idx++;
        }
    }

    // ─── REVERSE ──────────────────────────────────────────────────

    /**
     * O(n) — in-place reversal using three pointers.
     * Used in admin view to show "newest added" first.
     *
     * RECURRENCE RELATION:
     *   T(n) = T(n-1) + O(1)  →  T(n) = O(n)
     */
    public void reverse() {
        Node prev = null;
        Node cur  = head;
        while (cur != null) {
            Node next = cur.next;
            cur.next  = prev;
            prev      = cur;
            cur       = next;
        }
        head = prev;
    }

    // ─── CYCLE DETECTION ──────────────────────────────────────────

    /**
     * Floyd's Cycle Detection — O(n), O(1) space.
     * Slow pointer moves 1 step, fast pointer moves 2 steps.
     * If they ever meet, there's a cycle.
     *
     * CO2: Demonstrates cycle detection as required by the ADT syllabus.
     */
    public boolean hasCycle() {
        Node slow = head;
        Node fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    // ─── UTILITY ──────────────────────────────────────────────────

    public int     size()    { return size; }
    public boolean isEmpty() { return head == null; }
    public Node    getHead() { return head; }

    /** Convert list to Saree array (useful for sorting) */
    public Saree[] toArray() {
        Saree[] arr = new Saree[size];
        Node    cur = head;
        int     i   = 0;
        while (cur != null) { arr[i++] = cur.data; cur = cur.next; }
        return arr;
    }

    /** Rebuild list from sorted array */
    public static SinglyLinkedList fromArray(Saree[] arr) {
        SinglyLinkedList list = new SinglyLinkedList();
        for (Saree s : arr) list.insertAtTail(s);
        return list;
    }
}
