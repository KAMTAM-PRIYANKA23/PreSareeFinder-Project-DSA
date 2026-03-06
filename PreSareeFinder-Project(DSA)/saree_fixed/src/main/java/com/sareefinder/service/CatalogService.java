package com.sareefinder.service;

import com.sareefinder.auth.AuthManager;
import com.sareefinder.ds.hashtable.SareeHashTableChaining;
import com.sareefinder.ds.linkedlist.SinglyLinkedList;
import com.sareefinder.ds.heap.MaxHeapDiscount;
import com.sareefinder.ds.heap.PopularityPriorityQueue;
import com.sareefinder.algorithms.search.SearchAlgorithms;
import com.sareefinder.algorithms.sort.SortingAlgorithms;
import com.sareefinder.model.Saree;
import java.util.*;

/**
 * CATALOG SERVICE — CO4, CO6
 * ===========================
 * The central repository for all sarees.
 * Combines multiple data structures for different access patterns:
 *
 *   STRUCTURE                    │ PURPOSE
 *   ─────────────────────────────┼──────────────────────────────────
 *   SareeHashTableChaining       │ O(1) lookup by ID, CRUD operations
 *   SinglyLinkedList             │ Full catalogue traversal
 *   HashMap<String, List<Saree>> │ Store → sarees index (Java Collections)
 *   HashMap<String, List<Saree>> │ Type  → sarees index
 *   Saree[] sortedById           │ Binary search by ID
 *   Saree[] sortedByPrice        │ Binary search by price range
 *   MaxHeapDiscount              │ Top discount deals
 *   PopularityPriorityQueue      │ Most popular sarees
 *
 * CO6: Multiple data structures cooperating in one application.
 */
public class CatalogService {

    // ─── Primary storage (CO4: hash table with chaining) ──────────
    private SareeHashTableChaining hashTable;

    // ─── Java Collections (CO4) ───────────────────────────────────
    private Map<String, List<Saree>> storeIndex;   // store name → sarees
    private Map<String, List<Saree>> typeIndex;    // type → sarees

    // ─── Linked list for traversal (CO2) ──────────────────────────
    private SinglyLinkedList catalogue;

    // ─── Sorted arrays for binary search (CO1) ────────────────────
    private Saree[] sortedById;
    private Saree[] sortedByPrice;
    private boolean indexDirty = true;  // rebuild when data changes

    // ─── Heap / Priority Queue (CO3) ──────────────────────────────
    private MaxHeapDiscount      discountHeap;
    private PopularityPriorityQueue popularityQueue;

    private int nextId = 25; // auto-increment ID counter

    // ─── Constructor ──────────────────────────────────────────────

    public CatalogService() {
        hashTable       = new SareeHashTableChaining();
        storeIndex      = new HashMap<>();
        typeIndex       = new HashMap<>();
        catalogue       = new SinglyLinkedList();
        discountHeap    = new MaxHeapDiscount(200);
        popularityQueue = new PopularityPriorityQueue(200);
        seedData();
    }

    // ─── SEED DATA ────────────────────────────────────────────────

    private void seedData() {
        Saree[] initial = {
            // ── Kalaniketan ──
            new Saree(1,  "Bridal Kanjivaram",       "Wedding",    AuthManager.STORE_KALANIKETAN, 25000, 12, true,  "Premium bridal Kanjivaram with temple border and rich pallu"),
            new Saree(2,  "Kanchipuram Silk Saree",  "Silk",       AuthManager.STORE_KALANIKETAN,  8500,  0, true,  "Pure Kanchipuram silk with zari border"),
            new Saree(3,  "Cotton Printed Daily",    "Cotton",     AuthManager.STORE_KALANIKETAN,  1000, 15, true,  "Lightweight cotton saree with floral prints"),
            new Saree(4,  "Net Embroidered Fancy",   "Designer",   AuthManager.STORE_KALANIKETAN,  5000, 16, true,  "Stunning net saree with heavy embroidery"),

            // ── Chennai Shopping Mall ──
            new Saree(5,  "Banarasi Silk Weave",     "Silk",       AuthManager.STORE_CHENNAI,     12000,  0, true,  "Exquisite Banarasi silk with intricate golden brocade"),
            new Saree(6,  "Soft Silk Pattu",         "Silk",       AuthManager.STORE_CHENNAI,      6500, 20, true,  "Soft silk saree in rich jewel tones"),
            new Saree(7,  "Pure Linen Comfortable",  "Cotton",     AuthManager.STORE_CHENNAI,      2200,  0, true,  "Breathable pure linen saree"),
            new Saree(8,  "Paithani Silk Saree",     "Wedding",    AuthManager.STORE_CHENNAI,     15000,  0, false, "Authentic Paithani silk with peacock motifs"),

            // ── South India Shopping Mall ──
            new Saree(9,  "Pochampally Ikat Saree",  "Traditional",AuthManager.STORE_SOUTH_INDIA,  4000, 20, true,  "Authentic Pochampally ikat weave"),
            new Saree(10, "Uppada Silk Pattu",       "Silk",       AuthManager.STORE_SOUTH_INDIA, 11000, 11, true,  "Traditional Uppada silk with fine golden zari"),
            new Saree(11, "Mangalagiri Cotton",      "Cotton",     AuthManager.STORE_SOUTH_INDIA,  1100,  0, true,  "Crisp Mangalagiri cotton saree with classic border"),
            new Saree(12, "Ikkat Double Weave",      "Traditional",AuthManager.STORE_SOUTH_INDIA,  3500, 20, true,  "Unique double-weave Ikkat with mirror patterns"),

            // ── She Needs ──
            new Saree(13, "Designer Georgette",      "Designer",   AuthManager.STORE_SHE_NEEDS,    4500,  0, true,  "Trendy georgette saree with embroidery and sequin work"),
            new Saree(14, "Chiffon Floral Fancy",    "Designer",   AuthManager.STORE_SHE_NEEDS,    1800,  0, true,  "Elegant chiffon saree with floral digital print"),
            new Saree(15, "Chanderi Silk Cotton",    "Silk",       AuthManager.STORE_SHE_NEEDS,    3800,  0, true,  "Delicate Chanderi saree with zari motifs"),
            new Saree(16, "Organza Silk Fancy",      "Designer",   AuthManager.STORE_SHE_NEEDS,    6200,  0, false, "Sheer organza silk with heavy stonework border"),

            // ── Manyavar ──
            new Saree(17, "Gadwal Silk Saree",       "Traditional",AuthManager.STORE_MANYAVAR,     6500,  0, true,  "Traditional Gadwal silk with cotton body and silk pallu"),
            new Saree(18, "Tussar Silk Natural",     "Silk",       AuthManager.STORE_MANYAVAR,     4800,  0, true,  "Natural Tussar silk with earthy tones"),
            new Saree(19, "Handloom Khadi Saree",    "Cotton",     AuthManager.STORE_MANYAVAR,      950,  0, true,  "Handspun khadi saree in solid colors"),
            new Saree(20, "Modal Silk Comfortable",  "Silk",       AuthManager.STORE_MANYAVAR,     1650,  0, true,  "Ultra-soft modal silk blend, wrinkle-free"),

            // ── Mangalya ──
            new Saree(21, "Bridal Silk Lehenga",     "Wedding",    AuthManager.STORE_MANGALYA,    20000, 10, true,  "Richly embroidered bridal silk for grand weddings"),
            new Saree(22, "Zari Border Cotton",      "Cotton",     AuthManager.STORE_MANGALYA,     1400,  0, true,  "Fine cotton saree with traditional zari border"),
            new Saree(23, "Kalamkari Printed",       "Traditional",AuthManager.STORE_MANGALYA,     3000, 13, true,  "Authentic Kalamkari hand-printed design"),
            new Saree(24, "Silk Blend Party Saree",  "Designer",   AuthManager.STORE_MANGALYA,     5500,  0, true,  "Lustrous silk blend saree perfect for parties"),
        };

        // Set sample popularity scores
        int[] popularity = {85,70,60,55,90,75,65,80,72,68,55,60,82,77,65,58,70,63,52,55,88,60,65,73};
        for (int i = 0; i < initial.length; i++) {
            initial[i].setPopularity(popularity[i]);
            addToAllStructures(initial[i]);
        }
    }

    // ─── ADD TO ALL STRUCTURES — O(1) amortized ───────────────────

    private void addToAllStructures(Saree s) {
        hashTable.insert(s);
        catalogue.insertAtTail(s);
        storeIndex.computeIfAbsent(s.getStore(), k -> new ArrayList<>()).add(s);
        typeIndex.computeIfAbsent(s.getType(),   k -> new ArrayList<>()).add(s);
        discountHeap.insert(s);
        popularityQueue.enqueue(s);
        indexDirty = true;
    }

    // ─── REBUILD SORTED INDEXES — O(n log n) ──────────────────────

    private void rebuildIndexes() {
        if (!indexDirty) return;
        Saree[] all = hashTable.getAll();
        sortedById    = SortingAlgorithms.sortedCopy(all, SortingAlgorithms.BY_ID_ASC);
        sortedByPrice = SortingAlgorithms.sortedCopy(all, SortingAlgorithms.BY_PRICE_ASC);
        indexDirty    = false;
    }

    // ═══════════════════════════════════════════════════════════════
    // ADMIN OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /** O(1) average — hash table insert */
    public void addSaree(String name, String type, String store,
                          double originalPrice, int discount,
                          boolean available, String desc) {
        Saree s = new Saree(nextId++, name, type, store, originalPrice,
                            discount, available, desc);
        addToAllStructures(s);
        System.out.println("  ✓ Saree added: " + s.getName() + " (ID=" + s.getId() + ")");
    }

    /** O(1) average — hash table delete */
    public boolean deleteSaree(int id, String adminStore) {
        Saree s = hashTable.searchById(id);
        if (s == null) { System.out.println("  ✗ Saree ID " + id + " not found."); return false; }
        if (!s.getStore().equals(adminStore)) {
            System.out.println("  ✗ You can only delete sarees from your own store.");
            return false;
        }
        hashTable.delete(id);
        catalogue.deleteById(id);
        storeIndex.getOrDefault(s.getStore(), Collections.emptyList()).removeIf(x -> x.getId() == id);
        typeIndex.getOrDefault(s.getType(), Collections.emptyList()).removeIf(x -> x.getId() == id);
        indexDirty = true;
        System.out.println("  ✓ Deleted: " + s.getName());
        return true;
    }

    /** O(1) average */
    public boolean updatePrice(int id, double newPrice, String adminStore) {
        Saree s = hashTable.searchById(id);
        if (s == null || !s.getStore().equals(adminStore)) return false;
        s.setOriginalPrice(newPrice);
        indexDirty = true;
        System.out.printf("  ✓ Price updated: %s → ₹%.0f%n", s.getName(), s.getPrice());
        return true;
    }

    /** O(1) average */
    public boolean updateDiscount(int id, int discount, String adminStore) {
        Saree s = hashTable.searchById(id);
        if (s == null || !s.getStore().equals(adminStore)) return false;
        s.setDiscount(discount);
        indexDirty = true;
        System.out.printf("  ✓ Discount updated: %s → %d%%%n", s.getName(), discount);
        return true;
    }

    /** O(1) average */
    public boolean updateStock(int id, boolean available, String adminStore) {
        Saree s = hashTable.searchById(id);
        if (s == null || !s.getStore().equals(adminStore)) return false;
        s.setAvailable(available);
        System.out.printf("  ✓ Stock updated: %s → %s%n",
            s.getName(), available ? "In Stock" : "Out of Stock");
        return true;
    }

    // ═══════════════════════════════════════════════════════════════
    // QUERY / FILTER OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    /** O(1) average — hash table lookup */
    public Saree findById(int id) { return hashTable.searchById(id); }

    /** O(n) — linear search, substring match */
    public Saree[] searchByName(String keyword) {
        return SearchAlgorithms.linearSearchByName(hashTable.getAll(), keyword);
    }

    /** O(log n + k) — binary search on sorted-by-price array */
    public Saree[] findByPriceRange(double min, double max) {
        rebuildIndexes();
        return SearchAlgorithms.binarySearchPriceRange(sortedByPrice, min, max);
    }

    /** O(n/k) average — hash table filter, k=number of stores */
    public Saree[] findByStore(String store) {
        return hashTable.filterByStore(store);
    }

    /** O(n) — must scan all for type match */
    public Saree[] findByType(String type) {
        return hashTable.filterByType(type);
    }

    /** Combined filter: store + type + price range */
    public Saree[] findFiltered(String store, String type,
                                 double minPrice, double maxPrice) {
        Saree[] pool = (store != null && !store.isEmpty())
            ? hashTable.filterByStore(store)
            : hashTable.getAll();
        java.util.List<Saree> result = new java.util.ArrayList<>();
        for (Saree s : pool) {
            boolean matchType  = (type  == null || type.isEmpty()  || s.getType().equalsIgnoreCase(type));
            boolean matchPrice = s.getPrice() >= minPrice && s.getPrice() <= maxPrice;
            boolean inStock    = s.isAvailable();
            if (matchType && matchPrice && inStock) result.add(s);
        }
        return result.toArray(new Saree[0]);
    }

    /** O(n) — all sarees */
    public Saree[] getAll() { return hashTable.getAll(); }

    /** O(log n) — binary search by ID (requires sorted array) */
    public Saree binaryFindById(int id) {
        rebuildIndexes();
        return SearchAlgorithms.binarySearchById(sortedById, id);
    }

    // ─── SORTED VIEWS ─────────────────────────────────────────────

    /** Sort by chosen field using chosen algorithm */
    public Saree[] getSortedBy(String field, String algorithm) {
        Saree[] arr = hashTable.getAll().clone();
        java.util.Comparator<Saree> cmp = switch (field.toLowerCase()) {
            case "price_asc"     -> SortingAlgorithms.BY_PRICE_ASC;
            case "price_desc"    -> SortingAlgorithms.BY_PRICE_DESC;
            case "discount"      -> SortingAlgorithms.BY_DISCOUNT_DESC;
            case "name"          -> SortingAlgorithms.BY_NAME_ASC;
            default              -> SortingAlgorithms.BY_ID_ASC;
        };
        switch (algorithm.toLowerCase()) {
            case "bubble"    -> SortingAlgorithms.bubbleSort(arr, cmp);
            case "selection" -> SortingAlgorithms.selectionSort(arr, cmp);
            case "insertion" -> SortingAlgorithms.insertionSort(arr, cmp);
            case "merge"     -> SortingAlgorithms.mergeSort(arr, cmp);
            default          -> SortingAlgorithms.quickSort(arr, cmp);
        }
        return arr;
    }

    // ─── HEAP / PRIORITY QUEUE VIEWS ─────────────────────────────

    public void showTopDiscounts(int k) { discountHeap.displayTopDeals(k); }
    public void showTopPopular(int k)   { popularityQueue.displayTopPopular(k); }

    // ─── ADMIN: VIEW OWN STORE ────────────────────────────────────

    public Saree[] getByStore(String store) {
        return storeIndex.getOrDefault(store, Collections.emptyList())
                         .toArray(new Saree[0]);
    }

    // ─── DISPLAY ──────────────────────────────────────────────────

    public static void printSareeTable(Saree[] sarees, String header) {
        if (sarees == null || sarees.length == 0) {
            System.out.println("  (No sarees found)");
            return;
        }
        System.out.println("\n  ══ " + header + " (" + sarees.length + " items) ══");
        System.out.printf("  %-4s %-30s %-14s %-28s %-28s %s%n",
            "ID", "Name", "Type", "Store", "Price", "Stock");
        System.out.println("  " + "─".repeat(115));
        for (Saree s : sarees) System.out.println(s);
        System.out.println("  " + "─".repeat(115));
    }

    // ─── DISPLAY HASH TABLE STRUCTURE ─────────────────────────────
    public void showHashTableStructure() { hashTable.displayStructure(); }

    // ─── LINKED LIST TRAVERSAL ────────────────────────────────────
    public void traverseCatalogue() {
        System.out.println("\n  Catalogue via Singly Linked List traversal:");
        catalogue.traverse();
    }

    // ─── LINKED LIST: CYCLE CHECK ────────────────────────────────
    public void checkCycle() {
        boolean has = catalogue.hasCycle();
        System.out.println("  Cycle detected in catalogue list: " + has + " (expected: false)");
    }

    // ─── ALL STORE NAMES ──────────────────────────────────────────
    public Set<String> getAllStores() { return storeIndex.keySet(); }
    public Set<String> getAllTypes()  { return typeIndex.keySet(); }
    public int         totalCount()   { return hashTable.size(); }
}
