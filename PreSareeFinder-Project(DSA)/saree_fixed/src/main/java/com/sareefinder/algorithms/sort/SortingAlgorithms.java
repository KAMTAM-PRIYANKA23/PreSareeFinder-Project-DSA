package com.sareefinder.algorithms.sort;

import com.sareefinder.model.Saree;
import java.util.Comparator;

/**
 * SORTING ALGORITHMS — CO1
 * =========================
 * Five sorting algorithms with full complexity analysis.
 *
 * ┌───────────────┬──────────┬──────────┬──────────┬────────┬──────────────────────────────────────┐
 * │ Algorithm     │ Best Ω   │ Average Θ│ Worst O  │ Space  │ When to use                          │
 * ├───────────────┼──────────┼──────────┼──────────┼────────┼──────────────────────────────────────┤
 * │ Bubble Sort   │ Ω(n)     │ Θ(n²)    │ O(n²)    │ O(1)   │ Nearly sorted, tiny n, teaching only │
 * │ Selection Sort│ Ω(n²)    │ Θ(n²)    │ O(n²)    │ O(1)   │ Minimize swaps, small n              │
 * │ Insertion Sort│ Ω(n)     │ Θ(n²)    │ O(n²)    │ O(1)   │ Nearly sorted, online (stream) data  │
 * │ Merge Sort    │ Ω(n logn)│ Θ(n logn)│ O(n logn)│ O(n)   │ Stable sort, large n, linked lists   │
 * │ Quick Sort    │ Ω(n logn)│ Θ(n logn)│ O(n²)    │ O(logn)│ General purpose, large n, in-place   │
 * └───────────────┴──────────┴──────────┴──────────┴────────┴──────────────────────────────────────┘
 *
 * RECURRENCE RELATIONS (CO1):
 *   Bubble/Selection/Insertion: T(n) = T(n-1) + O(n) => O(n²)
 *   Merge Sort:  T(n) = 2T(n/2) + O(n)  => O(n log n)  [Master Theorem case 2]
 *   Quick Sort:  T(n) = 2T(n/2) + O(n)  => O(n log n)  average
 *                T(n) = T(n-1) + O(n)   => O(n²)        worst (sorted input, bad pivot)
 *
 * All sorts in this class work on Saree[] and accept a Comparator<Saree>
 * so they can sort by price, discount, name, or any field.
 */
public class SortingAlgorithms {

    // ═══════════════════════════════════════════════════════════════
    // BUBBLE SORT — O(n²)
    // ═══════════════════════════════════════════════════════════════

    /**
     * BUBBLE SORT
     * ────────────
     * Repeatedly swap adjacent elements if out of order.
     * Largest unsorted element "bubbles" to the right each pass.
     *
     * Best case Ω(n) — already sorted (with early-exit optimisation).
     * Worst case O(n²) — reverse sorted.
     *
     * Optimisation: if no swaps in a pass, array is sorted → exit early.
     * Without this optimisation, best case is also O(n²).
     *
     * CO1: Demonstrates quadratic growth. Good for teaching comparison sorts.
     */
    public static void bubbleSort(Saree[] arr, Comparator<Saree> cmp) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {  // inner shrinks each pass
                if (cmp.compare(arr[j], arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break;  // early exit — Ω(n) best case
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // SELECTION SORT — O(n²)
    // ═══════════════════════════════════════════════════════════════

    /**
     * SELECTION SORT
     * ───────────────
     * Find the minimum in the unsorted portion and swap it to the front.
     * Always makes exactly n-1 swaps regardless of input order.
     *
     * Best = Average = Worst = O(n²) — no early exit possible.
     * Space: O(1) in-place.
     *
     * Advantage over Bubble: fewer swaps (good when swap is expensive).
     *
     * CO1: Shows O(n²) even with optimised swap count.
     */
    public static void selectionSort(Saree[] arr, Comparator<Saree> cmp) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {  // find min in [i+1..n-1]
                if (cmp.compare(arr[j], arr[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) swap(arr, i, minIdx);  // at most 1 swap per outer pass
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // INSERTION SORT — O(n²) worst, O(n) best
    // ═══════════════════════════════════════════════════════════════

    /**
     * INSERTION SORT
     * ───────────────
     * Build sorted portion one element at a time, "inserting" each
     * new element into its correct position in the sorted prefix.
     *
     * Best case Ω(n): already sorted — inner loop never executes.
     * Worst case O(n²): reverse sorted — every element shifts all the way.
     *
     * Excellent for:
     *   • Nearly-sorted arrays (admin adds a few new sarees)
     *   • Online sorting (sarees arrive one at a time in a stream)
     *   • Small arrays (n < 20) — low constant factor
     *   • Used internally by Timsort for small runs
     *
     * CO1: Demonstrates adaptive sorting — sensitive to input order.
     */
    public static void insertionSort(Saree[] arr, Comparator<Saree> cmp) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            Saree key = arr[i];
            int   j   = i - 1;
            // Shift elements greater than key one position right
            while (j >= 0 && cmp.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // MERGE SORT — O(n log n) guaranteed
    // ═══════════════════════════════════════════════════════════════

    /**
     * MERGE SORT
     * ───────────
     * Divide array in half, recursively sort each half, merge them.
     *
     * RECURRENCE: T(n) = 2T(n/2) + O(n)
     *   By Master Theorem: a=2, b=2, f(n)=n, log_b(a)=1, n^1=n
     *   Case 2 (f(n) = Θ(n^log_b a)) → T(n) = Θ(n log n)
     *
     * Best = Average = Worst = O(n log n) — guaranteed!
     * Space: O(n) — merge requires auxiliary array.
     *
     * STABLE: equal elements maintain original relative order.
     * Preferred when stability matters (sort by name then by price).
     *
     * CO1: Demonstrates divide-and-conquer and Master Theorem application.
     */
    public static void mergeSort(Saree[] arr, Comparator<Saree> cmp,
                                  int left, int right) {
        if (left >= right) return;                  // base case: 1 element
        int mid = left + (right - left) / 2;
        mergeSort(arr, cmp, left, mid);             // sort left half
        mergeSort(arr, cmp, mid + 1, right);        // sort right half
        merge(arr, cmp, left, mid, right);          // merge both halves
    }

    private static void merge(Saree[] arr, Comparator<Saree> cmp,
                               int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Saree[] L = new Saree[n1];
        Saree[] R = new Saree[n2];
        System.arraycopy(arr, left,     L, 0, n1);
        System.arraycopy(arr, mid + 1,  R, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (cmp.compare(L[i], R[j]) <= 0) arr[k++] = L[i++]; // stable: <=
            else                               arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    /** Convenience wrapper */
    public static void mergeSort(Saree[] arr, Comparator<Saree> cmp) {
        if (arr.length > 1) mergeSort(arr, cmp, 0, arr.length - 1);
    }

    // ═══════════════════════════════════════════════════════════════
    // QUICK SORT — O(n log n) average, O(n²) worst
    // ═══════════════════════════════════════════════════════════════

    /**
     * QUICK SORT
     * ───────────
     * Choose pivot, partition array around it, recursively sort parts.
     *
     * RECURRENCE (average, random pivot):
     *   T(n) = 2T(n/2) + O(n) => O(n log n)
     * RECURRENCE (worst, sorted input with first/last pivot):
     *   T(n) = T(n-1) + O(n) => O(n²)
     *
     * Pivot strategy: MEDIAN-OF-THREE (first, mid, last elements).
     * This avoids worst case on already-sorted inputs.
     *
     * Space: O(log n) average (call stack depth).
     * NOT stable: equal elements may swap.
     *
     * WHY faster than Merge Sort in practice:
     *   • Better cache locality (in-place partitioning)
     *   • Lower constant factor
     *   • Average case is consistently O(n log n)
     *
     * CO1: Demonstrates partition-based sorting, pivot strategy,
     *       and worst-case avoidance.
     */
    public static void quickSort(Saree[] arr, Comparator<Saree> cmp,
                                  int low, int high) {
        if (low >= high) return;

        // Median-of-three pivot selection
        int mid = low + (high - low) / 2;
        medianOfThree(arr, cmp, low, mid, high);
        // After medianOfThree: arr[mid] is the median, swap to arr[high-1]
        // Standard Lomuto partition uses arr[high] as pivot
        swap(arr, mid, high);

        int pivotIdx = partition(arr, cmp, low, high);
        quickSort(arr, cmp, low,          pivotIdx - 1);
        quickSort(arr, cmp, pivotIdx + 1, high);
    }

    private static int partition(Saree[] arr, Comparator<Saree> cmp,
                                  int low, int high) {
        Saree pivot = arr[high]; // Lomuto scheme
        int   i     = low - 1;

        for (int j = low; j < high; j++) {
            if (cmp.compare(arr[j], pivot) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    /** Sort low, mid, high so that arr[mid] is the median */
    private static void medianOfThree(Saree[] arr, Comparator<Saree> cmp,
                                       int low, int mid, int high) {
        if (cmp.compare(arr[low], arr[mid])  > 0) swap(arr, low, mid);
        if (cmp.compare(arr[low], arr[high]) > 0) swap(arr, low, high);
        if (cmp.compare(arr[mid], arr[high]) > 0) swap(arr, mid, high);
    }

    /** Convenience wrapper */
    public static void quickSort(Saree[] arr, Comparator<Saree> cmp) {
        if (arr.length > 1) quickSort(arr, cmp, 0, arr.length - 1);
    }

    // ═══════════════════════════════════════════════════════════════
    // READY-MADE COMPARATORS for this system
    // ═══════════════════════════════════════════════════════════════

    /** Sort by price ascending */
    public static final Comparator<Saree> BY_PRICE_ASC =
        (a, b) -> Double.compare(a.getPrice(), b.getPrice());

    /** Sort by price descending */
    public static final Comparator<Saree> BY_PRICE_DESC =
        (a, b) -> Double.compare(b.getPrice(), a.getPrice());

    /** Sort by discount descending (highest discount first) */
    public static final Comparator<Saree> BY_DISCOUNT_DESC =
        (a, b) -> Integer.compare(b.getDiscount(), a.getDiscount());

    /** Sort by name alphabetically */
    public static final Comparator<Saree> BY_NAME_ASC =
        (a, b) -> a.getName().compareToIgnoreCase(b.getName());

    /** Sort by ID ascending (required for binary search) */
    public static final Comparator<Saree> BY_ID_ASC =
        (a, b) -> Integer.compare(a.getId(), b.getId());

    // ═══════════════════════════════════════════════════════════════
    // ALGORITHM COMPARISON DISPLAY — CO1
    // ═══════════════════════════════════════════════════════════════

    public static void printComplexityTable() {
        System.out.println("\n  ╔══ SORTING ALGORITHM COMPLEXITY (CO1) ══════════════════════════════════╗");
        System.out.println("  ║ Algorithm     │ Best Ω     │ Average Θ  │ Worst O    │ Space  │ Stable ║");
        System.out.println("  ║───────────────┼────────────┼────────────┼────────────┼────────┼────────║");
        System.out.println("  ║ Bubble Sort   │ Ω(n)       │ Θ(n²)      │ O(n²)      │ O(1)   │ Yes    ║");
        System.out.println("  ║ Selection Sort│ Ω(n²)      │ Θ(n²)      │ O(n²)      │ O(1)   │ No     ║");
        System.out.println("  ║ Insertion Sort│ Ω(n)       │ Θ(n²)      │ O(n²)      │ O(1)   │ Yes    ║");
        System.out.println("  ║ Merge Sort    │ Ω(n log n) │ Θ(n log n) │ O(n log n) │ O(n)   │ Yes    ║");
        System.out.println("  ║ Quick Sort    │ Ω(n log n) │ Θ(n log n) │ O(n²)      │ O(logn)│ No     ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════════════════════╝");
    }

    // ─── UTILITY ──────────────────────────────────────────────────
    private static void swap(Saree[] arr, int i, int j) {
        Saree t = arr[i]; arr[i] = arr[j]; arr[j] = t;
    }

    /** Defensive copy so original isn't modified */
    public static Saree[] sortedCopy(Saree[] arr, Comparator<Saree> cmp) {
        Saree[] copy = arr.clone();
        mergeSort(copy, cmp);  // stable, guaranteed O(n log n)
        return copy;
    }
}
