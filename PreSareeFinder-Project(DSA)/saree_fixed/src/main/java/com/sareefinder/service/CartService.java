package com.sareefinder.service;

import com.sareefinder.ds.linkedlist.DoublyLinkedList;
import com.sareefinder.ds.queue.OrderQueue;
import com.sareefinder.model.Saree;

/**
 * CART SERVICE — CO3, CO5
 * ========================
 * Manages the shopping cart and billing.
 *
 * DATA STRUCTURES USED:
 *   DoublyLinkedList — cart storage
 *     Why doubly? O(1) delete from any position when node is known.
 *     Allows "undo last add" in O(1) using tail pointer.
 *
 *   OrderQueue (CircularQueue) — order processing
 *     When user places order, all cart items are enqueued.
 *     Orders processed FIFO — fair and sequential.
 *
 * BILLING LOGIC (as per requirement):
 *   • Shows bill for each saree individually
 *   • Shows combined total
 *   • Applies GST (18% on designer/silk, 5% on cotton/traditional)
 *   • Applies shipping if total < ₹3000
 *
 * CO5: Practical application of linear data structures.
 * CO6: Cart + Queue + LinkedList working together.
 *
 * BIG-O:
 *   addToCart     O(1)  — insert at tail (doubly linked list)
 *   removeFromCart O(n) — search then O(1) delete
 *   viewCart      O(n)  — traverse
 *   generateBill  O(n)  — traverse once
 *   placeOrder    O(n)  — enqueue each item
 */
public class CartService {

    private DoublyLinkedList cart;
    private OrderQueue       orderQueue;
    private String           ownerUsername;

    public CartService(String username) {
        this.cart          = new DoublyLinkedList();
        this.orderQueue    = new OrderQueue();
        this.ownerUsername = username;
    }

    // ─── ADD TO CART — O(1) ───────────────────────────────────────

    public boolean addToCart(Saree saree) {
        if (!saree.isAvailable()) {
            System.out.println("  ✗ \"" + saree.getName() + "\" is currently out of stock.");
            return false;
        }
        // Check duplicate
        if (cart.searchById(saree.getId()) != null) {
            System.out.println("  ✗ \"" + saree.getName() + "\" is already in your cart.");
            return false;
        }
        cart.insertAtTail(saree);                        // O(1)
        System.out.println("  ✓ Added to cart: " + saree.getName()
            + " | ₹" + (int) saree.getPrice());
        return true;
    }

    // ─── REMOVE FROM CART — O(n) ──────────────────────────────────

    public boolean removeFromCart(int sareeId) {
        boolean removed = cart.deleteById(sareeId);     // O(n) search, O(1) delete
        if (removed) System.out.println("  ✓ Removed from cart (ID=" + sareeId + ")");
        else         System.out.println("  ✗ Saree ID " + sareeId + " not in cart.");
        return removed;
    }

    // ─── VIEW CART — O(n) ─────────────────────────────────────────

    public void viewCart() {
        System.out.println("\n  ╔══ YOUR CART (" + cart.size() + " items) ══════════════════════════╗");
        if (cart.isEmpty()) {
            System.out.println("  ║  Cart is empty. Browse sarees and add some!          ║");
            System.out.println("  ╚══════════════════════════════════════════════════════╝");
            return;
        }
        cart.traverseForward();
        System.out.printf("%n  ╔══ CART TOTAL: ₹%-8.0f ═══════════════════════════════╗%n",
            cart.totalPrice());
        System.out.println("  ╚══════════════════════════════════════════════════════╝");
    }

    // ─── GENERATE BILL — O(n) ─────────────────────────────────────

    /**
     * BILLING REQUIREMENT (as specified):
     *   1. First saree  — individual bill
     *   2. Second saree — individual bill
     *   3. Combined bill with taxes and shipping
     *
     * GST rates:
     *   Silk, Wedding, Designer → 12% GST
     *   Cotton, Traditional     → 5%  GST
     */
    public void generateBill() {
        if (cart.isEmpty()) {
            System.out.println("  Cart is empty. Add sarees before generating a bill.");
            return;
        }

        Saree[] items = cart.toArray();

        System.out.println("\n  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║           SAREE FINDER SYSTEM — INVOICE                  ║");
        System.out.println("  ║══════════════════════════════════════════════════════════║");
        System.out.println("  ║  Customer: " + padRight(ownerUsername, 46) + "║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");

        double grandTotal = 0;
        double totalGST   = 0;

        for (int i = 0; i < items.length; i++) {
            Saree  s         = items[i];
            double basePrice = s.getPrice();
            double gstRate   = getGSTRate(s.getType());
            double gst       = Math.round(basePrice * gstRate);
            double total     = basePrice + gst;

            grandTotal += total;
            totalGST   += gst;

            String ordinal = (i == 0) ? "FIRST" : (i == 1) ? "SECOND"
                : (i == 2) ? "THIRD" : ("ITEM " + (i + 1));

            System.out.println("\n  ── " + ordinal + " SAREE BILL ───────────────────────────────────");
            System.out.printf("  %-28s : %s%n",  "Saree Name",     s.getName());
            System.out.printf("  %-28s : %s%n",  "Type",           s.getType());
            System.out.printf("  %-28s : %s%n",  "Store",          s.getStore());
            System.out.printf("  %-28s : ₹%.0f%n","Original Price", s.getOriginalPrice());
            if (s.getDiscount() > 0) {
                System.out.printf("  %-28s : -%d%% (saving ₹%.0f)%n",
                    "Discount", s.getDiscount(), s.getOriginalPrice() - basePrice);
            }
            System.out.printf("  %-28s : ₹%.0f%n","Sale Price",  basePrice);
            System.out.printf("  %-28s : %.0f%% → ₹%.0f%n","GST", gstRate * 100, gst);
            System.out.printf("  %-28s : ₹%.0f%n","─── Subtotal ────────────────────", total);
        }

        // Shipping
        double shipping = grandTotal < 3000 ? 150 : 0;

        System.out.println("\n  ═══════════════════════════════════════════════════════════");
        System.out.println("  COMBINED BILL SUMMARY");
        System.out.println("  ═══════════════════════════════════════════════════════════");
        System.out.printf("  %-28s : %d items%n",    "Total Items", items.length);
        System.out.printf("  %-28s : ₹%.0f%n",       "Items Subtotal", grandTotal - totalGST);
        System.out.printf("  %-28s : ₹%.0f%n",       "Total GST",  totalGST);
        System.out.printf("  %-28s : ₹%.0f%s%n",     "Shipping", shipping,
            shipping == 0 ? " (FREE above ₹3000)" : " (free above ₹3000)");
        System.out.println("  ───────────────────────────────────────────────────────────");
        System.out.printf("  %-28s : ₹%.0f%n",       "GRAND TOTAL", grandTotal + shipping);
        System.out.println("  ═══════════════════════════════════════════════════════════");
        System.out.println("  Thank you for shopping at Hyderabad Saree Finder! 🪷");
    }

    // ─── PLACE ORDER — O(n) ───────────────────────────────────────

    /**
     * Moves all cart items into the OrderQueue (CircularQueue).
     * Cart is cleared after placement.
     * Queue processes orders FIFO.
     *
     * CO3: Queue for order processing.
     */
    public void placeOrder() {
        if (cart.isEmpty()) {
            System.out.println("  Cart is empty. Add sarees before placing an order.");
            return;
        }
        Saree[] items = cart.toArray();
        System.out.println("\n  ── Placing order for " + items.length + " item(s)... ──");
        for (Saree s : items) {
            orderQueue.enqueue(s);
            System.out.println("  ✓ Queued: " + s.getName());
        }

        // Clear cart
        cart.clear();
        System.out.println("  ✓ Order placed! Cart cleared.");
        System.out.println("  ✓ Your order is in the processing queue.");
    }

    // ─── VIEW ORDER QUEUE ─────────────────────────────────────────
    public void viewOrderQueue() { orderQueue.display(); }

    // ─── PROCESS NEXT ORDER ───────────────────────────────────────
    public void processNextOrder() {
        Saree s = orderQueue.dequeue();
        if (s != null) System.out.println("  ✓ Processed order: " + s.getName());
    }

    // ─── UTILITY ──────────────────────────────────────────────────

    private double getGSTRate(String type) {
        return switch (type.toLowerCase()) {
            case "silk", "wedding", "designer" -> 0.12;
            default                            -> 0.05;
        };
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public int     cartSize()    { return cart.size(); }
    public boolean cartIsEmpty() { return cart.isEmpty(); }
    public DoublyLinkedList getCart() { return cart; }
}
