package com.sareefinder.ui;

import com.sareefinder.algorithms.search.SearchAlgorithms;
import com.sareefinder.ds.linkedlist.CircularLinkedList;
import com.sareefinder.ds.stack.NavigationStack;
import com.sareefinder.model.Saree;
import com.sareefinder.model.User;
import com.sareefinder.service.CartService;
import com.sareefinder.service.CatalogService;
import com.sareefinder.service.WishlistService;
import com.sareefinder.util.DisplayUtil;

import java.util.Scanner;

/**
 * USER MENU — CO5, CO6
 * =====================
 * Console UI for customer interactions.
 *
 * DATA STRUCTURES IN USE HERE:
 *   NavigationStack   — tracks which menu the user came from (CO3)
 *   CircularLinkedList— featured sarees carousel display (CO2)
 *   WishlistService   → WishlistStack (CO3)
 *   CartService       → DoublyLinkedList + OrderQueue (CO3, CO5)
 *   CatalogService    → HashTable + SortedArrays (CO1, CO4)
 */
public class UserMenu {

    private final User             user;
    private final CatalogService   catalog;
    private final CartService      cart;
    private final WishlistService  wishlist;
    private final NavigationStack  navHistory;
    private final Scanner          sc;

    // Active filters (persist across sub-menus)
    private String  filterStore    = "";
    private String  filterType     = "";
    private double  filterMinPrice = 0;
    private double  filterMaxPrice = Double.MAX_VALUE;

    public UserMenu(User user, CatalogService catalog, Scanner sc) {
        this.user       = user;
        this.catalog    = catalog;
        this.cart       = new CartService(user.getUsername());
        this.wishlist   = new WishlistService();
        this.navHistory = new NavigationStack();
        this.sc         = sc;
    }

    // ─── MAIN LOOP ────────────────────────────────────────────────

    public void show() {
        System.out.println("\n  Welcome, " + user.getName() + "! 🪷");
        showFeaturedSarees();

        navHistory.push("MAIN MENU");
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readInt("  Enter choice: ");

            switch (choice) {
                case 1  -> { navHistory.push("SELECT MALL");   selectMall(); }
                case 2  -> { navHistory.push("SELECT BUDGET"); selectBudget(); }
                case 3  -> { navHistory.push("SELECT TYPE");   selectType(); }
                case 4  -> { navHistory.push("VIEW SAREES");   viewSarees(); }
                case 5  -> { navHistory.push("SEARCH");        searchSarees(); }
                case 6  -> { navHistory.push("WISHLIST");      manageWishlist(); }
                case 7  -> { navHistory.push("CART");          manageCart(); }
                case 8  -> cart.generateBill();
                case 9  -> showRecentlyViewed();
                case 10 -> clearFilters();
                case 0  -> running = false;
                default -> DisplayUtil.printError("Invalid option. Try again.");
            }
        }
        System.out.println("  Thank you for visiting, " + user.getName() + "! 🪷");
    }

    private void printMainMenu() {
        System.out.println();
        DisplayUtil.printDivider("MAIN MENU — " + user.getName());
        printActiveFilters();
        System.out.println("   1. Select Shopping Mall");
        System.out.println("   2. Select Budget Range");
        System.out.println("   3. Select Type of Saree");
        System.out.println("   4. View Sarees");
        System.out.println("   5. Search Sarees by Keyword");
        System.out.println("   6. Wishlist  (" + wishlist.wishlistSize() + " items)");
        System.out.println("   7. Cart      (" + cart.cartSize() + " items)");
        System.out.println("   8. Generate Bill");
        System.out.println("   9. Recently Viewed");
        System.out.println("  10. Clear All Filters");
        System.out.println("   0. Logout");
    }

    private void printActiveFilters() {
        boolean any = !filterStore.isEmpty() || !filterType.isEmpty()
                      || filterMinPrice > 0 || filterMaxPrice < Double.MAX_VALUE;
        if (!any) return;
        System.out.print("  [Filters: ");
        if (!filterStore.isEmpty())       System.out.print("Mall=" + filterStore + " ");
        if (!filterType.isEmpty())        System.out.print("Type=" + filterType + " ");
        if (filterMinPrice > 0)           System.out.print("Min=₹" + (int)filterMinPrice + " ");
        if (filterMaxPrice < Double.MAX_VALUE) System.out.print("Max=₹" + (int)filterMaxPrice);
        System.out.println("]");
    }

    // ─── 1. SELECT MALL ───────────────────────────────────────────

    private void selectMall() {
        DisplayUtil.printDivider("SELECT SHOPPING MALL");
        String[] malls = {
            "Kalaniketan",
            "Chennai Shopping Mall",
            "South India Shopping Mall",
            "She Needs",
            "Manyavar",
            "Mangalya"
        };
        for (int i = 0; i < malls.length; i++) {
            int count = catalog.getByStore(malls[i]).length;
            System.out.printf("   %d. %-32s (%d sarees)%n", i + 1, malls[i], count);
        }
        System.out.println("   0. Any Mall (no filter)");
        int choice = readInt("  Select: ");

        if (choice == 0) {
            filterStore = "";
            DisplayUtil.printSuccess("Mall filter cleared.");
        } else if (choice >= 1 && choice <= malls.length) {
            filterStore = malls[choice - 1];
            DisplayUtil.printSuccess("Mall set to: " + filterStore);
            DisplayUtil.printDSANote("Hash Table filterByStore() — O(n) scan, O(n/k) average");
        } else {
            DisplayUtil.printError("Invalid selection.");
        }
    }

    // ─── 2. SELECT BUDGET ─────────────────────────────────────────

    private void selectBudget() {
        DisplayUtil.printDivider("SELECT BUDGET RANGE");
        System.out.println("   1. ₹1,000 – ₹3,000   (Budget-friendly)");
        System.out.println("   2. ₹3,000 – ₹5,000   (Mid-range)");
        System.out.println("   3. ₹5,000 – ₹7,000   (Premium)");
        System.out.println("   4. ₹7,000+            (Luxury)");
        System.out.println("   5. Custom range");
        System.out.println("   0. Any price");
        int choice = readInt("  Select: ");

        switch (choice) {
            case 0 -> { filterMinPrice = 0; filterMaxPrice = Double.MAX_VALUE;
                        DisplayUtil.printSuccess("Price filter cleared."); }
            case 1 -> { filterMinPrice = 1000;  filterMaxPrice = 3000; }
            case 2 -> { filterMinPrice = 3000;  filterMaxPrice = 5000; }
            case 3 -> { filterMinPrice = 5000;  filterMaxPrice = 7000; }
            case 4 -> { filterMinPrice = 7000;  filterMaxPrice = Double.MAX_VALUE; }
            case 5 -> {
                filterMinPrice = readDouble("  Min price (₹): ");
                filterMaxPrice = readDouble("  Max price (₹): ");
            }
            default -> { DisplayUtil.printError("Invalid selection."); return; }
        }

        if (choice != 0) {
            DisplayUtil.printSuccess("Budget set: ₹" + (int)filterMinPrice
                + " – " + (filterMaxPrice == Double.MAX_VALUE ? "any" : "₹"+(int)filterMaxPrice));
            DisplayUtil.printDSANote("Binary search on price-sorted array — O(log n + k)");
        }
    }

    // ─── 3. SELECT TYPE ───────────────────────────────────────────

    private void selectType() {
        DisplayUtil.printDivider("SELECT TYPE OF SAREE");
        String[] types = {"Silk", "Cotton", "Designer", "Wedding", "Traditional"};
        for (int i = 0; i < types.length; i++) {
            System.out.printf("   %d. %s%n", i + 1, types[i]);
        }
        System.out.println("   0. All Types");
        int choice = readInt("  Select: ");

        if (choice == 0) {
            filterType = "";
            DisplayUtil.printSuccess("Type filter cleared.");
        } else if (choice >= 1 && choice <= types.length) {
            filterType = types[choice - 1];
            DisplayUtil.printSuccess("Type set to: " + filterType);
        } else {
            DisplayUtil.printError("Invalid selection.");
        }
    }

    // ─── 4. VIEW SAREES ───────────────────────────────────────────

    private void viewSarees() {
        double maxP = filterMaxPrice == Double.MAX_VALUE ? 999999 : filterMaxPrice;
        Saree[] sarees = catalog.findFiltered(filterStore, filterType, filterMinPrice, maxP);

        if (sarees.length == 0) {
            DisplayUtil.printInfo("No sarees match your filters. Try adjusting them.");
            return;
        }

        printDetailedSareeList(sarees, "ALL AVAILABLE SAREES");

        System.out.println("\n  What would you like to do?");
        System.out.println("   1. Add to Wishlist");
        System.out.println("   2. Add to Cart");
        System.out.println("   3. View saree details");
        System.out.println("   0. Back");
        int choice = readInt("  Choice: ");

        switch (choice) {
            case 1 -> addToWishlistById(sarees);
            case 2 -> addToCartById(sarees);
            case 3 -> viewDetails(sarees);
        }
    }

    /**
     * Prints a detailed catalog listing showing Saree Code, Name, Type,
     * Store, Price, Discount, and Stock status clearly.
     */
    private void printDetailedSareeList(Saree[] sarees, String header) {
        System.out.println("\n  ╔══════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("  ║  %-88s║%n", " 🪷 " + header + " (" + sarees.length + " sarees)");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("  ║  %-8s  %-30s  %-13s  %-22s  %-14s  %-5s  %-5s  ║%n",
            "CODE", "SAREE NAME", "TYPE", "STORE", "PRICE", "DISC%", "STOCK");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════════════════════════════╣");

        // Group by type for clarity
        String[] types = {"Wedding", "Silk", "Cotton", "Designer", "Traditional"};
        boolean anyPrinted = false;

        for (String type : types) {
            boolean headerPrinted = false;
            for (Saree s : sarees) {
                if (!s.getType().equalsIgnoreCase(type)) continue;
                if (!headerPrinted) {
                    System.out.printf("  ║  %-88s║%n", "  ▸ " + type.toUpperCase() + " SAREES");
                    System.out.println("  ║  " + "─".repeat(88) + "║");
                    headerPrinted = true;
                    anyPrinted = true;
                }
                String code    = "SAR-" + String.format("%03d", s.getId());
                String price   = String.format("₹%.0f", s.getPrice());
                String origPr  = s.getDiscount() > 0
                    ? String.format("₹%.0f→%s", s.getOriginalPrice(), price) : price;
                String disc    = s.getDiscount() > 0 ? "-" + s.getDiscount() + "%" : "  —  ";
                String stock   = s.isAvailable() ? "✓" : "✗ OOS";
                System.out.printf("  ║  %-8s  %-30s  %-13s  %-22s  %-14s  %-5s  %-5s  ║%n",
                    code,
                    truncate(s.getName(), 30),
                    truncate(s.getType(), 13),
                    truncate(s.getStore(), 22),
                    truncate(origPr, 14),
                    disc,
                    stock);
            }
            if (headerPrinted) System.out.println("  ║  " + "─".repeat(88) + "║");
        }

        if (!anyPrinted) System.out.printf("  ║  %-88s║%n", "  (No sarees match current filters)");
        System.out.println("  ╚══════════════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("  💡 Use the CODE (e.g. SAR-001) or numeric ID to add to Wishlist / Cart.");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    // ─── 5. SEARCH ────────────────────────────────────────────────

    private void searchSarees() {
        DisplayUtil.printDivider("SEARCH SAREES BY KEYWORD");
        System.out.println("  Searches across: name, type, store, and description.");
        System.out.println("  Examples: \"wedding\", \"silk\", \"bridal\", \"cotton\", \"banarasi\"");
        System.out.println();
        System.out.print("  Enter keyword: ");
        sc.nextLine();
        String keyword = sc.nextLine().trim();
        if (keyword.isEmpty()) return;

        System.out.println();
        DisplayUtil.printDSANote("Linear Search — O(n), substring match across name+type+description+store");

        Saree[] results = catalog.searchByName(keyword);

        if (results.length == 0) {
            DisplayUtil.printInfo("No sarees found for \"" + keyword + "\"");
            DisplayUtil.printInfo("Try: silk / cotton / wedding / designer / traditional / banarasi / bridal / kanjivaram");
            return;
        }

        // Detect if keyword maps to a known type for a more informative header
        String[] knownTypes = {"Wedding", "Silk", "Cotton", "Designer", "Traditional"};
        String matchedType = null;
        for (String t : knownTypes) {
            if (t.toLowerCase().contains(keyword.toLowerCase())
                    || keyword.toLowerCase().contains(t.toLowerCase())) {
                matchedType = t;
                break;
            }
        }

        String header = matchedType != null
            ? "SEARCH RESULTS — " + matchedType.toUpperCase() + " SAREES for \"" + keyword + "\""
            : "SEARCH RESULTS for \"" + keyword + "\"";

        printDetailedSareeList(results, header);

        // Summary by type
        java.util.Map<String, Long> typeCounts = new java.util.LinkedHashMap<>();
        for (Saree s : results) {
            typeCounts.merge(s.getType(), 1L, Long::sum);
        }
        System.out.print("  📊 Found: ");
        typeCounts.forEach((t, c) -> System.out.print(c + " " + t + "  "));
        System.out.println();

        System.out.println("\n   1. Add to Wishlist   2. Add to Cart   0. Back");
        int ch = readInt("  Choice: ");
        if (ch == 1) addToWishlistById(results);
        if (ch == 2) addToCartById(results);
    }

    // ─── 6. WISHLIST ──────────────────────────────────────────────

    private void manageWishlist() {
        boolean running = true;
        while (running) {
            wishlist.viewWishlist();
            System.out.println("\n   1. Add by ID    2. Remove by ID");
            System.out.println("   3. Move top item to Cart");
            System.out.println("   4. View Recently Viewed");
            System.out.println("   0. Back");
            int choice = readInt("  Choice: ");
            switch (choice) {
                case 1 -> {
                    CatalogService.printSareeTable(catalog.getAll(), "ALL SAREES");
                    int id = readInt("  Enter Saree ID to wishlist: ");
                    Saree s = catalog.findById(id);
                    if (s != null) { wishlist.addToWishlist(s); wishlist.markViewed(s); }
                    else DisplayUtil.printError("Saree ID " + id + " not found.");
                }
                case 2 -> {
                    int id = readInt("  Enter Saree ID to remove: ");
                    wishlist.removeFromWishlist(id);
                }
                case 3 -> {
                    Saree s = wishlist.moveTopToCart();
                    if (s != null) cart.addToCart(s);
                }
                case 4  -> wishlist.viewRecentlyViewed();
                case 0  -> running = false;
                default -> DisplayUtil.printError("Invalid option.");
            }
        }
        DisplayUtil.printDSANote("Stack: push/pop O(1), removeById O(n). Deque: insertRear/deleteFront O(1)");
    }

    // ─── 7. CART ──────────────────────────────────────────────────

    private void manageCart() {
        boolean running = true;
        while (running) {
            cart.viewCart();
            System.out.println("\n   1. Add by ID    2. Remove by ID");
            System.out.println("   3. Place Order  4. View Order Queue");
            System.out.println("   5. Generate Bill");
            System.out.println("   0. Back");
            int choice = readInt("  Choice: ");
            switch (choice) {
                case 1 -> {
                    double maxP = filterMaxPrice == Double.MAX_VALUE ? 999999 : filterMaxPrice;
                    Saree[] shown = catalog.findFiltered(filterStore, filterType, filterMinPrice, maxP);
                    if (shown.length == 0) shown = catalog.getAll();
                    CatalogService.printSareeTable(shown, "AVAILABLE SAREES");
                    addToCartById(shown);
                }
                case 2 -> { int id = readInt("  Enter Saree ID: "); cart.removeFromCart(id); }
                case 3 -> cart.placeOrder();
                case 4 -> cart.viewOrderQueue();
                case 5 -> cart.generateBill();
                case 0 -> running = false;
                default -> DisplayUtil.printError("Invalid option.");
            }
        }
        DisplayUtil.printDSANote("Doubly LinkedList: insertTail O(1), deleteById O(n). CircularQueue: enqueue/dequeue O(1)");
    }

    // ─── 9. RECENTLY VIEWED ───────────────────────────────────────

    private void showRecentlyViewed() {
        wishlist.viewRecentlyViewed();
        DisplayUtil.printDSANote("Deque sliding window — insertRear O(1), deleteFront O(1)");
    }

    // ─── 10. CLEAR FILTERS ────────────────────────────────────────

    private void clearFilters() {
        filterStore = ""; filterType = "";
        filterMinPrice = 0; filterMaxPrice = Double.MAX_VALUE;
        DisplayUtil.printSuccess("All filters cleared.");
    }

    // ─── FEATURED SAREES (Circular Linked List) ───────────────────

    private void showFeaturedSarees() {
        Saree[] all = catalog.getAll();
        CircularLinkedList featured = new CircularLinkedList();
        // Insert 5 high-discount sarees into circular list
        int added = 0;
        for (Saree s : all) {
            if (s.getDiscount() > 0 && added < 5) { featured.insert(s); added++; }
        }
        if (!featured.isEmpty()) {
            System.out.println("\n  ╔══ FEATURED DEALS — TODAY'S TOP OFFERS ══════════════╗");
            featured.traverse();
            System.out.println("  ╚══════════════════════════════════════════════════════╝");
            DisplayUtil.printDSANote("Circular LinkedList — O(1) insert, O(n) traverse, intentional cycle");
        }
    }

    // ─── DETAIL VIEW ──────────────────────────────────────────────

    private void viewDetails(Saree[] sarees) {
        int id = readInt("  Enter Saree ID for details: ");
        for (Saree s : sarees) {
            if (s.getId() == id) {
                System.out.println("\n  ┌──────────────────────────────────────────┐");
                System.out.printf("  │ %-42s│%n", s.getName());
                System.out.println("  ├──────────────────────────────────────────┤");
                System.out.printf("  │ Type     : %-30s│%n", s.getType());
                System.out.printf("  │ Store    : %-30s│%n", s.getStore());
                System.out.printf("  │ Price    : ₹%-29.0f│%n", s.getPrice());
                if (s.getDiscount() > 0)
                    System.out.printf("  │ Discount : -%d%% (was ₹%-24.0f│%n",
                        s.getDiscount(), s.getOriginalPrice());
                System.out.printf("  │ Stock    : %-30s│%n",
                    s.isAvailable() ? "✓ In Stock" : "✗ Out of Stock");
                System.out.printf("  │ Desc     : %-30s│%n", s.getDescription());
                System.out.println("  └──────────────────────────────────────────┘");
                wishlist.markViewed(s);
                return;
            }
        }
        DisplayUtil.printError("Saree ID " + id + " not found in current view.");
    }

    // ─── SHARED HELPERS ───────────────────────────────────────────

    private void addToWishlistById(Saree[] displayed) {
        System.out.print("  Enter Saree ID or Code (e.g. 1 or SAR-001) to add to wishlist: ");
        String input = sc.nextLine().trim();
        int id = parseSareeId(input);
        if (id < 0) { DisplayUtil.printError("Invalid ID format."); return; }
        Saree found = null;
        for (Saree s : displayed) if (s.getId() == id) { found = s; break; }
        if (found == null) found = catalog.findById(id);
        if (found == null) { DisplayUtil.printError("Saree ID " + id + " not found."); return; }
        wishlist.addToWishlist(found);
        wishlist.markViewed(found);
        DisplayUtil.printDSANote("Stack push — O(1)");
    }

    private void addToCartById(Saree[] displayed) {
        System.out.print("  Enter Saree ID or Code (e.g. 1 or SAR-001) to add to cart: ");
        String input = sc.nextLine().trim();
        int id = parseSareeId(input);
        if (id < 0) { DisplayUtil.printError("Invalid ID format."); return; }
        Saree found = null;
        for (Saree s : displayed) if (s.getId() == id) { found = s; break; }
        if (found == null) found = catalog.findById(id);
        if (found == null) { DisplayUtil.printError("Saree ID " + id + " not found."); return; }
        cart.addToCart(found);
        wishlist.markViewed(found);
        DisplayUtil.printDSANote("DoublyLinkedList insertAtTail — O(1)");
    }

    /** Parses plain integer IDs ("5") and SAR-XXX codes ("SAR-005") */
    private int parseSareeId(String input) {
        try {
            if (input.toUpperCase().startsWith("SAR-")) {
                return Integer.parseInt(input.substring(4).trim());
            }
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int readInt(String prompt) {
        if (!prompt.isEmpty()) System.out.print(prompt);
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (Exception e) { return 0; }
    }
}
