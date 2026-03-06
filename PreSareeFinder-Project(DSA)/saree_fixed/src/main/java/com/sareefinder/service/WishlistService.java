package com.sareefinder.service;

import com.sareefinder.ds.stack.WishlistStack;
import com.sareefinder.ds.queue.SareeDeque;
import com.sareefinder.model.Saree;

/**
 * WISHLIST SERVICE — CO3, CO5
 * ============================
 * Manages the user's saved/liked sarees.
 *
 * DATA STRUCTURES USED:
 *   WishlistStack (array-based stack) — primary wishlist
 *     LIFO: most recently wished item shown first
 *     push/pop/peek all O(1)
 *
 *   SareeDeque — "Recently Viewed" list
 *     Add to rear when user views a saree
 *     Remove from front when list exceeds 5 items (sliding window)
 *     Used to show "You recently viewed..." feature
 *
 * CO3: Stack for wishlist, Deque for recently viewed.
 * CO5: Practical wishlist application.
 *
 * BIG-O SUMMARY:
 *   addToWishlist()    O(1)  — stack push
 *   removeFromWishlist O(n)  — must rebuild stack (no random access)
 *   viewWishlist()     O(n)  — display all
 *   markViewed()       O(1)  — deque insertRear
 */
public class WishlistService {

    private static final int RECENTLY_VIEWED_MAX = 5;

    private WishlistStack wishlist;
    private SareeDeque    recentlyViewed;

    public WishlistService() {
        wishlist       = new WishlistStack();
        recentlyViewed = new SareeDeque();
    }

    // ─── ADD TO WISHLIST — O(1) ───────────────────────────────────

    public boolean addToWishlist(Saree saree) {
        boolean added = wishlist.push(saree);
        if (added) System.out.println("  ♡ Added to wishlist: " + saree.getName());
        return added;
    }

    // ─── REMOVE FROM WISHLIST — O(n) ──────────────────────────────

    public boolean removeFromWishlist(int sareeId) {
        boolean removed = wishlist.removeById(sareeId);
        if (removed) System.out.println("  ✓ Removed from wishlist (ID=" + sareeId + ")");
        else         System.out.println("  ✗ ID " + sareeId + " not in wishlist.");
        return removed;
    }

    // ─── VIEW WISHLIST — O(n) ─────────────────────────────────────

    public void viewWishlist() {
        System.out.println("\n  ╔══ YOUR WISHLIST ══════════════════════════════════════╗");
        if (wishlist.isEmpty()) {
            System.out.println("  ║  Wishlist is empty. Heart a saree to save it!        ║");
            System.out.println("  ╚══════════════════════════════════════════════════════╝");
            return;
        }
        wishlist.display();
    }

    // ─── POP TOP WISHLIST ITEM ────────────────────────────────────

    public Saree popWishlist() {
        Saree s = wishlist.pop();
        if (s != null) System.out.println("  Removed from top of wishlist: " + s.getName());
        return s;
    }

    // ─── PEEK TOP ITEM — O(1) ─────────────────────────────────────

    public Saree peekWishlist() { return wishlist.peek(); }

    // ─── MARK AS RECENTLY VIEWED (Deque) — O(1) ──────────────────

    /**
     * Sliding window of last RECENTLY_VIEWED_MAX sarees.
     * Add to rear, remove from front when full.
     * Uses Deque for O(1) operations at both ends.
     */
    public void markViewed(Saree saree) {
        if (recentlyViewed.size() >= RECENTLY_VIEWED_MAX) {
            recentlyViewed.deleteFront();    // O(1) — remove oldest
        }
        recentlyViewed.insertRear(saree);    // O(1) — add newest
    }

    // ─── VIEW RECENTLY VIEWED ─────────────────────────────────────

    public void viewRecentlyViewed() {
        System.out.println("\n  ── Recently Viewed (last " + RECENTLY_VIEWED_MAX + ") ──");
        if (recentlyViewed.isEmpty()) {
            System.out.println("  No recently viewed sarees.");
            return;
        }
        recentlyViewed.display();
    }

    // ─── MOVE FROM WISHLIST TO CART (convenience) ─────────────────

    /**
     * Pop from wishlist and return for adding to cart.
     * Stack pop → cart add demonstrates stack as temporary buffer.
     * O(1) for the pop, then O(1) for cart insert.
     */
    public Saree moveTopToCart() {
        return wishlist.pop();
    }

    // ─── UTILITY ──────────────────────────────────────────────────

    public int     wishlistSize()    { return wishlist.size(); }
    public boolean wishlistIsEmpty() { return wishlist.isEmpty(); }
    public boolean isInWishlist(int id) { return wishlist.contains(id); }

    public Saree[] getWishlistItems() { return wishlist.toArray(); }
}
