package com.sareefinder.ui;

import com.sareefinder.algorithms.sort.SortingAlgorithms;
import com.sareefinder.model.Admin;
import com.sareefinder.model.Saree;
import com.sareefinder.service.CatalogService;
import com.sareefinder.util.DisplayUtil;

import java.util.Scanner;

/**
 * ADMIN MENU — CO6
 * =================
 * Console UI for store admins (salesmen).
 * Each admin can only manage sarees from their own store.
 *
 * Features: Add, Delete, Update Price, Update Discount,
 *           Update Stock, View All, Sort Sarees, DSA Demo
 */
public class AdminMenu {

    private final Admin          admin;
    private final CatalogService catalog;
    private final Scanner        sc;

    public AdminMenu(Admin admin, CatalogService catalog, Scanner sc) {
        this.admin   = admin;
        this.catalog = catalog;
        this.sc      = sc;
    }

    // ─── MAIN ADMIN LOOP ──────────────────────────────────────────

    public void show() {
        System.out.println("\n  Welcome, Admin " + admin.getName()
            + " | Store: " + admin.getStore());

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("  Enter choice: ");

            switch (choice) {
                case 1  -> viewMyInventory();
                case 2  -> addSaree();
                case 3  -> deleteSaree();
                case 4  -> updatePrice();
                case 5  -> updateDiscount();
                case 6  -> updateStock();
                case 7  -> sortSarees();
                case 8  -> viewAllSarees();
                case 9  -> showTopDeals();
                case 10 -> dsaDemo();
                case 0  -> running = false;
                default -> DisplayUtil.printError("Invalid option.");
            }
        }
        System.out.println("  Admin logout. Goodbye, " + admin.getName() + "!");
    }

    private void printMenu() {
        System.out.println();
        DisplayUtil.printDivider("ADMIN PANEL — " + admin.getStore());
        System.out.println("   1. View My Inventory");
        System.out.println("   2. Add Saree");
        System.out.println("   3. Delete Saree");
        System.out.println("   4. Update Price");
        System.out.println("   5. Update Discount");
        System.out.println("   6. Update Stock Availability");
        System.out.println("   7. Sort Sarees");
        System.out.println("   8. View All Sarees (all stores)");
        System.out.println("   9. Show Top Deals (Max-Heap)");
        System.out.println("  10. DSA Demonstration");
        System.out.println("   0. Logout");
    }

    // ─── 1. VIEW INVENTORY ────────────────────────────────────────

    private void viewMyInventory() {
        Saree[] sarees = catalog.getByStore(admin.getStore());
        CatalogService.printSareeTable(sarees, "MY INVENTORY — " + admin.getStore());
        DisplayUtil.printDSANote("Hash Table filter by store — O(n/k) average");
    }

    // ─── 2. ADD SAREE ─────────────────────────────────────────────

    private void addSaree() {
        DisplayUtil.printDivider("ADD NEW SAREE");

        sc.nextLine(); // flush
        System.out.print("  Saree Name   : "); String name  = sc.nextLine().trim();
        System.out.println("  Types: Silk | Cotton | Designer | Wedding | Traditional");
        System.out.print("  Type         : "); String type  = sc.nextLine().trim();
        System.out.print("  Original Price (₹): "); double price = readDouble();
        System.out.print("  Discount %   : "); int disc     = readInt("");
        System.out.print("  In Stock (Y/N): "); boolean avail = sc.nextLine().trim().equalsIgnoreCase("Y");
        System.out.print("  Description  : "); String desc  = sc.nextLine().trim();

        catalog.addSaree(name, type, admin.getStore(), price, disc, avail, desc);
        DisplayUtil.printDSANote("Hash Table insert — O(1) average");
    }

    // ─── 3. DELETE SAREE ──────────────────────────────────────────

    private void deleteSaree() {
        viewMyInventory();
        int id = readInt("  Enter Saree ID to delete: ");
        catalog.deleteSaree(id, admin.getStore());
        DisplayUtil.printDSANote("Hash Table delete — O(1) average; LinkedList delete — O(n)");
    }

    // ─── 4. UPDATE PRICE ──────────────────────────────────────────

    private void updatePrice() {
        viewMyInventory();
        int    id    = readInt("  Enter Saree ID: ");
        double price = readDouble("  New original price (₹): ");
        catalog.updatePrice(id, price, admin.getStore());
        DisplayUtil.printDSANote("Hash Table search + update — O(1) average");
    }

    // ─── 5. UPDATE DISCOUNT ───────────────────────────────────────

    private void updateDiscount() {
        viewMyInventory();
        int id   = readInt("  Enter Saree ID: ");
        int disc = readInt("  New discount % (0–90): ");
        catalog.updateDiscount(id, disc, admin.getStore());
        DisplayUtil.printDSANote("Hash Table search + update — O(1) average");
    }

    // ─── 6. UPDATE STOCK ──────────────────────────────────────────

    private void updateStock() {
        viewMyInventory();
        int id = readInt("  Enter Saree ID: ");
        sc.nextLine();
        System.out.print("  In Stock (Y/N): ");
        boolean avail = sc.nextLine().trim().equalsIgnoreCase("Y");
        catalog.updateStock(id, avail, admin.getStore());
        DisplayUtil.printDSANote("Hash Table search + update — O(1) average");
    }

    // ─── 7. SORT SAREES ───────────────────────────────────────────

    private void sortSarees() {
        DisplayUtil.printDivider("SORT SAREES");
        System.out.println("  Sort by:");
        System.out.println("   1. Price (Low → High)");
        System.out.println("   2. Price (High → Low)");
        System.out.println("   3. Discount (Highest first)");
        System.out.println("   4. Name (A → Z)");
        int sortChoice = readInt("  Choose field: ");

        System.out.println("\n  Algorithm:");
        System.out.println("   1. Bubble Sort   — O(n²)       best for tiny n");
        System.out.println("   2. Selection Sort— O(n²)       minimizes swaps");
        System.out.println("   3. Insertion Sort— O(n) best   best for nearly-sorted");
        System.out.println("   4. Merge Sort    — O(n log n)  stable, guaranteed");
        System.out.println("   5. Quick Sort    — O(n log n)  fastest in practice");
        int algChoice = readInt("  Choose algorithm: ");

        String field = switch (sortChoice) {
            case 1 -> "price_asc"; case 2 -> "price_desc";
            case 3 -> "discount";  case 4 -> "name";
            default -> "price_asc";
        };
        String alg = switch (algChoice) {
            case 1 -> "bubble";    case 2 -> "selection";
            case 3 -> "insertion"; case 4 -> "merge";
            default -> "quick";
        };

        Saree[] sorted = catalog.getSortedBy(field, alg);
        CatalogService.printSareeTable(sorted, "SORTED BY " + field.toUpperCase()
            + " using " + alg.toUpperCase() + " SORT");
        SortingAlgorithms.printComplexityTable();
    }

    // ─── 8. VIEW ALL SAREES ───────────────────────────────────────

    private void viewAllSarees() {
        Saree[] all = catalog.getAll();
        CatalogService.printSareeTable(all, "ALL SAREES — All Stores");
    }

    // ─── 9. TOP DEALS ─────────────────────────────────────────────

    private void showTopDeals() {
        catalog.showTopDiscounts(5);
        catalog.showTopPopular(5);
        DisplayUtil.printDSANote("Max-Heap extractMax — O(log n); Priority Queue dequeue — O(log n)");
    }

    // ─── 10. DSA DEMO ─────────────────────────────────────────────

    private void dsaDemo() {
        DisplayUtil.printDivider("DSA DEMONSTRATION (CO1–CO6)");
        System.out.println("\n  [CO1] Search & Sort Complexity:");
        com.sareefinder.algorithms.search.SearchAlgorithms.compareSearchDemo();
        SortingAlgorithms.printComplexityTable();

        System.out.println("\n  [CO2] Hash Table Structure:");
        catalog.showHashTableStructure();

        System.out.println("\n  [CO2] Cycle detection on catalogue list:");
        catalog.checkCycle();

        System.out.println("\n  [CO3] Top Discounted Sarees (Max-Heap):");
        catalog.showTopDiscounts(3);

        System.out.println("\n  [CO3] Top Popular Sarees (Priority Queue):");
        catalog.showTopPopular(3);
    }

    // ─── HELPERS ──────────────────────────────────────────────────

    private int readInt(String prompt) {
        System.out.print(prompt);
        try { int v = Integer.parseInt(sc.nextLine().trim()); return v; }
        catch (Exception e) { return -1; }
    }

    private double readDouble() { return readDouble(""); }
    private double readDouble(String prompt) {
        if (!prompt.isEmpty()) System.out.print(prompt);
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (Exception e) { return 0; }
    }
}
