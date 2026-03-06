package com.sareefinder;

import com.sareefinder.auth.AuthManager;
import com.sareefinder.model.Admin;
import com.sareefinder.model.User;
import com.sareefinder.service.CatalogService;
import com.sareefinder.ui.AdminMenu;
import com.sareefinder.ui.UserMenu;
import com.sareefinder.util.DisplayUtil;

import java.util.Scanner;

/**
 * MAIN APPLICATION — CO6
 * =======================
 * Entry point for the Saree Finder System.
 * Initialises all services and routes to the correct UI.
 *
 * APPLICATION ARCHITECTURE:
 * ┌──────────────────────────────────────────────────────────────┐
 * │                    Main (Entry Point)                        │
 * ├─────────────────────────┬────────────────────────────────────┤
 * │   AuthManager           │   CatalogService                  │
 * │   (Open Addressing HT)  │   (Chaining HT + LinkedList +     │
 * │                         │    SortedArrays + Heap + PQ)      │
 * ├─────────────────────────┴────────────────────────────────────┤
 * │   UserMenu                    AdminMenu                      │
 * │   ├── WishlistService         ├── Sorting Algorithms         │
 * │   │   (Stack + Deque)         ├── Search Algorithms          │
 * │   └── CartService             └── CatalogService (CRUD)      │
 * │       (DoublyLL + OrderQueue)                                │
 * └──────────────────────────────────────────────────────────────┘
 *
 * CO6: All data structures working together in one real application.
 *
 * DATA STRUCTURE → USAGE MAP:
 * ┌──────────────────────────┬─────────────────────────────────────┐
 * │ Data Structure           │ Used For                            │
 * ├──────────────────────────┼─────────────────────────────────────┤
 * │ Singly Linked List       │ Catalogue traversal (CO2)           │
 * │ Doubly Linked List       │ Cart management (CO2, CO5)          │
 * │ Circular Linked List     │ Featured sarees carousel (CO2)      │
 * │ Array                    │ WishlistStack, SortedArrays (CO2)   │
 * │ Stack                    │ Wishlist, NavigationHistory (CO3)   │
 * │ Circular Queue           │ Order processing (CO3)              │
 * │ Deque                    │ Recently Viewed (CO3)               │
 * │ Max-Heap                 │ Top discounts (CO3)                 │
 * │ Priority Queue           │ Popular sarees (CO3)                │
 * │ Hash Table (Chaining)    │ Saree lookup + CRUD (CO4)           │
 * │ Hash Table (Open Addr.)  │ User authentication (CO4)           │
 * │ HashMap (Java Coll.)     │ Store/type indexes (CO4)            │
 * │ Linear Search            │ Keyword/name search (CO1)           │
 * │ Binary Search            │ Price-range + ID lookup (CO1)       │
 * │ Bubble/Selection/Insertion Sort │ Admin sort options (CO1)     │
 * │ Merge Sort / Quick Sort  │ Admin sort options (CO1)            │
 * └──────────────────────────┴─────────────────────────────────────┘
 */
public class Main {

    private static final Scanner     sc      = new Scanner(System.in);
    private static final AuthManager auth    = new AuthManager();
    private static final CatalogService catalog = new CatalogService();

    public static void main(String[] args) {
        DisplayUtil.printBanner();
        auth.printCredentials();

        boolean appRunning = true;
        while (appRunning) {
            printLandingMenu();
            int choice = readInt("  Enter choice: ");

            switch (choice) {
                case 1  -> handleUserLogin();
                case 2  -> handleAdminLogin();
                case 3  -> handleRegister();
                case 4  -> showDSAOverview();
                case 0  -> { appRunning = false; System.out.println("\n  Goodbye! 🪷"); }
                default -> DisplayUtil.printError("Invalid option. Enter 1, 2, 3, 4, or 0.");
            }
        }
        sc.close();
    }

    // ─── LANDING MENU ─────────────────────────────────────────────

    private static void printLandingMenu() {
        System.out.println();
        DisplayUtil.printDivider("HYDERABAD SAREE FINDER SYSTEM");
        System.out.println("   1. Customer Login");
        System.out.println("   2. Admin (Salesman) Login");
        System.out.println("   3. New Customer Registration");
        System.out.println("   4. DSA Overview (CO1–CO6)");
        System.out.println("   0. Exit");
    }

    // ─── CUSTOMER LOGIN ───────────────────────────────────────────

    private static void handleUserLogin() {
        System.out.println();
        DisplayUtil.printDivider("CUSTOMER LOGIN");
        System.out.print("  Username: "); String username = sc.nextLine().trim();
        System.out.print("  Password: "); String password = sc.nextLine().trim();

        User user = auth.loginUser(username, password);
        if (user == null) {
            DisplayUtil.printError("Invalid username or password.");
            return;
        }

        DisplayUtil.printSuccess("Login successful! Welcome, " + user.getName());
        DisplayUtil.printDSANote("Auth via Open Addressing Hash Table — O(1) average lookup");

        new UserMenu(user, catalog, sc).show();
    }

    // ─── ADMIN LOGIN ──────────────────────────────────────────────

    private static void handleAdminLogin() {
        System.out.println();
        DisplayUtil.printDivider("ADMIN LOGIN");
        System.out.print("  Username: "); String username = sc.nextLine().trim();
        System.out.print("  Password: "); String password = sc.nextLine().trim();

        Admin admin = auth.loginAdmin(username, password);
        if (admin == null) {
            DisplayUtil.printError("Invalid admin credentials.");
            return;
        }

        DisplayUtil.printSuccess("Admin login successful! Store: " + admin.getStore());
        new AdminMenu(admin, catalog, sc).show();
    }

    // ─── REGISTER ─────────────────────────────────────────────────

    private static void handleRegister() {
        System.out.println();
        DisplayUtil.printDivider("NEW CUSTOMER REGISTRATION");
        System.out.print("  Choose username  : "); String username = sc.nextLine().trim();
        System.out.print("  Password         : "); String password = sc.nextLine().trim();
        System.out.print("  Your full name   : "); String name     = sc.nextLine().trim();
        System.out.print("  Phone number     : "); String phone    = sc.nextLine().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            DisplayUtil.printError("All fields are required.");
            return;
        }
        if (password.length() < 4) {
            DisplayUtil.printError("Password must be at least 4 characters.");
            return;
        }

        boolean success = auth.registerUser(username, password, name, phone);
        if (success) {
            DisplayUtil.printSuccess("Account created! You can now login as: " + username);
        } else {
            DisplayUtil.printError("Username \"" + username + "\" is already taken.");
        }
    }

    // ─── DSA OVERVIEW ─────────────────────────────────────────────

    private static void showDSAOverview() {
        System.out.println("\n");
        System.out.println("  ╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("  ║         DATA STRUCTURES & ALGORITHMS — COURSE OUTCOMES          ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("  ║ CO1 — Algorithm Analysis                                        ║");
        System.out.println("  ║   Big-O, Ω, Θ, Recurrence Relations                             ║");
        System.out.println("  ║   Linear Search O(n), Binary Search O(log n)                    ║");
        System.out.println("  ║   Bubble/Selection/Insertion O(n²), Merge/Quick O(n log n)      ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("  ║ CO2 — Abstract Data Types (ADTs)                                ║");
        System.out.println("  ║   Array  — WishlistStack (push/pop O(1)), SortedArrays          ║");
        System.out.println("  ║   Singly Linked List — Catalogue traversal, chaining buckets    ║");
        System.out.println("  ║   Doubly Linked List — Cart (O(1) delete with node reference)   ║");
        System.out.println("  ║   Circular Linked List — Featured sarees carousel               ║");
        System.out.println("  ║   Operations: insert, delete, search, traverse, reverse, cycle  ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("  ║ CO3 — Stacks and Queues                                         ║");
        System.out.println("  ║   Stack  — Wishlist (LIFO), Navigation history (back button)    ║");
        System.out.println("  ║   Circular Queue — Order processing (FIFO)                     ║");
        System.out.println("  ║   Deque  — Recently Viewed sliding window                       ║");
        System.out.println("  ║   Max-Heap — Top discounted sarees (extract-max O(log n))       ║");
        System.out.println("  ║   Priority Queue — Most popular sarees by score                 ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("  ║ CO4 — Hash-Based Data Structures                                ║");
        System.out.println("  ║   Chaining Hash Table — Saree catalogue (O(1) avg CRUD)         ║");
        System.out.println("  ║   Open Addressing HT — User auth (linear probing, tombstones)   ║");
        System.out.println("  ║   Java HashMap — Store/type index for O(1) group access         ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("  ║ CO5 — Practical Applications                                    ║");
        System.out.println("  ║   Cart  — DoublyLinkedList (insertTail O(1), delete O(1))       ║");
        System.out.println("  ║   Wishlist — Stack (push O(1), LIFO view)                       ║");
        System.out.println("  ║   Orders — CircularQueue (enqueue/dequeue O(1))                 ║");
        System.out.println("  ║   Billing — Per-item + combined + GST + shipping                ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("  ║ CO6 — Complete DSA Application                                  ║");
        System.out.println("  ║   All structures cooperate: auth → catalog → cart → billing     ║");
        System.out.println("  ║   HashTable lookup feeds DoublyLL cart feeds CircularQ orders   ║");
        System.out.println("  ║   Stack wishlist feeds DoublyLL cart, Heap shows best deals     ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════════╝");
    }

    private static int readInt(String prompt) {
        System.out.print(prompt);
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (Exception e) { return -1; }
    }
}
