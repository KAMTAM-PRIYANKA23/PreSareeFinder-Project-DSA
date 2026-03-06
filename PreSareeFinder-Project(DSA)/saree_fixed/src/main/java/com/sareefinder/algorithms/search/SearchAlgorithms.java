package com.sareefinder.algorithms.search;

import com.sareefinder.model.Saree;

/**
 * SEARCHING ALGORITHMS — CO1
 * ===========================
 * Two classical search algorithms with full complexity analysis.
 *
 * ┌─────────────────┬────────────┬────────────┬────────────┬──────────────────────────────────┐
 * │ Algorithm       │ Best Ω     │ Average Θ  │ Worst O    │ When to use                      │
 * ├─────────────────┼────────────┼────────────┼────────────┼──────────────────────────────────┤
 * │ Linear Search   │ Ω(1)       │ Θ(n/2)=Θ(n)│ O(n)       │ Unsorted data, small datasets    │
 * │ Binary Search   │ Ω(1)       │ Θ(log n)   │ O(log n)   │ SORTED data, large datasets      │
 * └─────────────────┴────────────┴────────────┴────────────┴──────────────────────────────────┘
 *
 * RECURRENCE RELATION for Binary Search:
 *   T(n) = T(n/2) + O(1)
 *   By Master Theorem (case 2 with a=1, b=2, f(n)=O(1)):
 *   T(n) = O(log n)
 *
 * Space Complexity:
 *   Linear Search: O(1) — no extra space
 *   Binary Search (iterative): O(1) — no stack frames
 *   Binary Search (recursive): O(log n) — call stack depth
 *
 * PRACTICAL GUIDE for this system:
 *   • Admin viewing sarees: binary search by ID (array is pre-sorted by ID)
 *   • Customer keyword search: linear search (must scan all names)
 *   • Price range: linear search (arbitrary filter condition)
 */
public class SearchAlgorithms {

    // ═══════════════════════════════════════════════════════════════
    // LINEAR SEARCH
    // ═══════════════════════════════════════════════════════════════

    /**
     * LINEAR SEARCH — by name (keyword match)
     * ─────────────────────────────────────────
     * Complexity: O(n) — must inspect every element
     * Best case Ω(1) — keyword found at index 0
     * Average  Θ(n/2) — found in the middle
     *
     * WHY linear here:
     *   Saree names are strings. Sorting by name and then binary
     *   searching would only find exact prefixes. Linear search
     *   allows SUBSTRING matching ("Silk" matches "Pure Silk Saree").
     *
     * @param arr     array of sarees to search
     * @param keyword substring to look for (case-insensitive)
     * @return array of matching sarees
     */
    public static Saree[] linearSearchByName(Saree[] arr, String keyword) {
        java.util.List<Saree> matches = new java.util.ArrayList<>();
        String kw = keyword.toLowerCase().trim();

        for (int i = 0; i < arr.length; i++) {          // O(n) comparisons
            if (arr[i].getName().toLowerCase().contains(kw)
                    || arr[i].getType().toLowerCase().contains(kw)
                    || arr[i].getStore().toLowerCase().contains(kw)
                    || arr[i].getDescription().toLowerCase().contains(kw)) {
                matches.add(arr[i]);
            }
        }
        return matches.toArray(new Saree[0]);
    }

    /**
     * LINEAR SEARCH — by price range
     * ─────────────────────────────────
     * Complexity: O(n) — no shortcut possible for range queries
     * on unsorted data.
     */
    public static Saree[] linearSearchByPriceRange(Saree[] arr,
                                                    double min, double max) {
        java.util.List<Saree> matches = new java.util.ArrayList<>();
        for (Saree s : arr) {
            if (s.getPrice() >= min && s.getPrice() <= max && s.isAvailable())
                matches.add(s);
        }
        return matches.toArray(new Saree[0]);
    }

    /**
     * LINEAR SEARCH — by store name
     * Complexity: O(n)
     */
    public static Saree[] linearSearchByStore(Saree[] arr, String store) {
        java.util.List<Saree> matches = new java.util.ArrayList<>();
        for (Saree s : arr) {
            if (s.getStore().equalsIgnoreCase(store)) matches.add(s);
        }
        return matches.toArray(new Saree[0]);
    }

    /**
     * LINEAR SEARCH — by type
     * Complexity: O(n)
     */
    public static Saree[] linearSearchByType(Saree[] arr, String type) {
        java.util.List<Saree> matches = new java.util.ArrayList<>();
        for (Saree s : arr) {
            if (s.getType().equalsIgnoreCase(type) && s.isAvailable())
                matches.add(s);
        }
        return matches.toArray(new Saree[0]);
    }

    // ═══════════════════════════════════════════════════════════════
    // BINARY SEARCH
    // ═══════════════════════════════════════════════════════════════

    /**
     * BINARY SEARCH — by ID (iterative)
     * ────────────────────────────────────
     * PRE-CONDITION: array MUST be sorted by ID in ascending order.
     *
     * Complexity: O(log n) — halves the search space each step
     * Best Ω(1) — target is the middle element
     *
     * RECURRENCE:  T(n) = T(n/2) + 1  =>  T(n) = O(log n)
     *
     * For n=1000 sarees:
     *   Linear: up to 1000 comparisons
     *   Binary: at most log₂(1000) ≈ 10 comparisons  ← 100x faster
     *
     * WHY iterative over recursive:
     *   Iterative: O(1) space — no call stack
     *   Recursive: O(log n) space — stack frames accumulate
     *
     * @param arr sorted array of sarees (sorted by ID)
     * @param id  target saree ID
     * @return Saree if found, null otherwise
     */
    public static Saree binarySearchById(Saree[] arr, int id) {
        int low  = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2; // avoid integer overflow

            if      (arr[mid].getId() == id) return arr[mid];   // found
            else if (arr[mid].getId() <  id) low  = mid + 1;    // go right
            else                             high = mid - 1;    // go left
        }
        return null; // not found
    }

    /**
     * BINARY SEARCH — by price (find first saree with price >= target)
     * Used for price-range lower-bound lookup on sorted array.
     * Complexity: O(log n)
     *
     * @param arr    array sorted by price ascending
     * @param price  lower bound of price range
     * @return index of first saree with price >= given price, or arr.length
     */
    public static int binarySearchLowerBound(Saree[] arr, double price) {
        int low = 0, high = arr.length;
        while (low < high) {
            int mid = (low + high) / 2;
            if (arr[mid].getPrice() < price) low  = mid + 1;
            else                             high = mid;
        }
        return low;
    }

    /**
     * BINARY SEARCH — by price upper bound.
     * Returns index of last saree with price <= target.
     */
    public static int binarySearchUpperBound(Saree[] arr, double price) {
        int low = 0, high = arr.length;
        while (low < high) {
            int mid = (low + high) / 2;
            if (arr[mid].getPrice() <= price) low  = mid + 1;
            else                              high = mid;
        }
        return low - 1;
    }

    /**
     * Combined: find all sarees within [minPrice, maxPrice]
     * using binary search on price-sorted array.
     * Complexity: O(log n + k) where k = number of results
     *             vs linear search O(n) — much faster for large n, small k
     */
    public static Saree[] binarySearchPriceRange(Saree[] sortedByPrice,
                                                  double min, double max) {
        int lo = binarySearchLowerBound(sortedByPrice, min);
        int hi = binarySearchUpperBound(sortedByPrice, max);

        if (lo > hi) return new Saree[0];

        java.util.List<Saree> result = new java.util.ArrayList<>();
        for (int i = lo; i <= hi; i++) {
            if (sortedByPrice[i].isAvailable()) result.add(sortedByPrice[i]);
        }
        return result.toArray(new Saree[0]);
    }

    // ─── RECURSIVE BINARY SEARCH (for CO1 demonstration) ─────────

    /**
     * RECURSIVE Binary Search — demonstrates recurrence relation.
     * T(n) = T(n/2) + O(1)  =>  O(log n)
     * Space: O(log n) due to call stack.
     */
    public static Saree binarySearchRecursive(Saree[] arr, int id,
                                               int low, int high) {
        if (low > high) return null; // base case

        int mid = low + (high - low) / 2;

        if      (arr[mid].getId() == id) return arr[mid];
        else if (arr[mid].getId() <  id) return binarySearchRecursive(arr, id, mid + 1, high);
        else                             return binarySearchRecursive(arr, id, low, mid - 1);
    }

    // ─── COMPARISON DEMO ──────────────────────────────────────────

    /**
     * Demonstrates the performance difference between linear and
     * binary search on arrays of increasing size.
     * CO1: Algorithm analysis and comparison.
     */
    public static void compareSearchDemo() {
        System.out.println("\n  ╔══ SEARCH ALGORITHM COMPARISON (CO1) ═════════════════╗");
        System.out.println("  ║  n (array size)  │ Linear Search │ Binary Search      ║");
        System.out.println("  ║────────────────────────────────────────────────────────║");
        int[] sizes = {10, 100, 1000, 10000, 100000};
        for (int n : sizes) {
            long linear = n;
            long binary = (long)(Math.log(n) / Math.log(2)) + 1;
            System.out.printf("  ║  n = %6d      │ %13d │ %18d ║%n",
                n, linear, binary);
        }
        System.out.println("  ╚════════════════════════════════════════════════════════╝");
    }
}
