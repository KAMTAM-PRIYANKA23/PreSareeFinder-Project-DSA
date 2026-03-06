package com.sareefinder.ds.linkedlist;

import com.sareefinder.model.Saree;

/**
 * DOUBLY LINKED LIST — CO2
 * ========================
 * Each node stores forward (next) and backward (prev) pointers.
 *
 * Used in this system for:
 *   • Cart management — allows efficient insertion/removal
 *     from both ends and the middle (CO5)
 *   • Navigation history — user can go "back" (CO3)
 *
 * BIG-O ANALYSIS:
 * ┌───────────────────────┬──────────┬───────────┐
 * │ Operation             │ Average  │ Worst     │
 * ├───────────────────────┼──────────┼───────────┤
 * │ insertAtHead()        │  O(1)    │  O(1)     │
 * │ insertAtTail()        │  O(1)*   │  O(1)*    │
 * │ deleteByValue()       │  O(n)    │  O(n)     │
 * │ deleteNode(node)      │  O(1)    │  O(1)     │ ← advantage over singly
 * │ search()              │  O(n)    │  O(n)     │
 * │ reverseTraverse()     │  O(n)    │  O(n)     │
 * └───────────────────────┴──────────┴───────────┘
 * *tail pointer maintained, so O(1)
 *
 * Space: O(n) — two extra pointers per node vs singly
 *
 * Advantage over Singly Linked List:
 *   → O(1) delete when you already have the node reference
 *   → Can traverse in reverse without reversing the list
 */
public class DoublyLinkedList {

    // ─── Node ─────────────────────────────────────────────────────
    public static class DNode {
        public Saree data;
        public DNode prev;
        public DNode next;

        public DNode(Saree data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    // ─── Fields ───────────────────────────────────────────────────
    private DNode head;
    private DNode tail;
    private int   size;

    public DoublyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    // ─── INSERT ───────────────────────────────────────────────────

    /** O(1) */
    public void insertAtHead(Saree saree) {
        DNode node = new DNode(saree);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head      = node;
        }
        size++;
    }

    /** O(1) — tail pointer makes this constant */
    public void insertAtTail(Saree saree) {
        DNode node = new DNode(saree);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail      = node;
        }
        size++;
    }

    // ─── DELETE ───────────────────────────────────────────────────

    /** O(n) to find, O(1) to remove once found */
    public boolean deleteById(int id) {
        DNode cur = head;
        while (cur != null) {
            if (cur.data.getId() == id) {
                unlinkNode(cur);
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    /** O(1) — given a direct node reference */
    private void unlinkNode(DNode node) {
        if (node.prev != null) node.prev.next = node.next;
        else                   head           = node.next;

        if (node.next != null) node.next.prev = node.prev;
        else                   tail           = node.prev;

        size--;
    }

    // ─── TRAVERSE ─────────────────────────────────────────────────

    /** O(n) forward */
    public void traverseForward() {
        if (head == null) { System.out.println("  Cart is empty."); return; }
        DNode cur = head;
        int   i   = 1;
        while (cur != null) {
            System.out.println("  " + i + ". " + cur.data.toShortString());
            cur = cur.next;
            i++;
        }
    }

    /** O(n) backward — demonstrates prev pointer advantage */
    public void traverseBackward() {
        if (tail == null) return;
        DNode cur = tail;
        while (cur != null) {
            System.out.println("  " + cur.data);
            cur = cur.prev;
        }
    }

    // ─── SEARCH ───────────────────────────────────────────────────

    public Saree searchById(int id) {
        DNode cur = head;
        while (cur != null) {
            if (cur.data.getId() == id) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    // ─── UTILITY ──────────────────────────────────────────────────

    public int     size()    { return size; }
    public boolean isEmpty() { return head == null; }
    public DNode   getHead() { return head; }
    public DNode   getTail() { return tail; }

    /** O(1) — reset list */
    public void clear() { head = null; tail = null; size = 0; }

    public Saree[] toArray() {
        Saree[] arr = new Saree[size];
        DNode   cur = head;
        int     i   = 0;
        while (cur != null) { arr[i++] = cur.data; cur = cur.next; }
        return arr;
    }

    /** Total price of all items in cart — O(n) */
    public double totalPrice() {
        double total = 0;
        DNode  cur   = head;
        while (cur != null) {
            total += cur.data.getPrice();
            cur    = cur.next;
        }
        return total;
    }
}
