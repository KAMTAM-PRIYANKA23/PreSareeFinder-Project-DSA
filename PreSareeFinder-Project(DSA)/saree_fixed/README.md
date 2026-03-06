# 🪷 Hyderabad Saree Finder System
### Java Console Application — Data Structures & Algorithms Academic Project

---

## How to Compile and Run

```bash
# From the project root (SareeFinderSystem/)
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.sareefinder.Main
```

**Requirements:** Java 17 or higher (uses switch expressions from Java 14+)

---

## Demo Credentials

| Role     | Username              | Password  | Store                    |
|----------|-----------------------|-----------|--------------------------|
| Customer | user1                 | demo123   | —                        |
| Customer | anita                 | demo123   | —                        |
| Admin    | admin_kalaniketan     | admin123  | Kalaniketan              |
| Admin    | admin_chennai         | admin123  | Chennai Shopping Mall    |
| Admin    | admin_southindia      | admin123  | South India Shopping Mall|
| Admin    | admin_sheneeds        | admin123  | She Needs                |
| Admin    | admin_manyavar        | admin123  | Manyavar                 |
| Admin    | admin_mangalya        | admin123  | Mangalya                 |

---

## Project Structure

```
SareeFinderSystem/
└── src/main/java/com/sareefinder/
    ├── Main.java                          # Entry point, app router
    ├── model/
    │   ├── Saree.java                     # Core entity — used in ALL data structures
    │   ├── User.java                      # Customer account
    │   └── Admin.java                     # Store manager account
    ├── ds/
    │   ├── linkedlist/
    │   │   ├── SinglyLinkedList.java      # CO2 — Catalogue traversal
    │   │   ├── DoublyLinkedList.java      # CO2 — Cart (O(1) delete)
    │   │   └── CircularLinkedList.java    # CO2 — Featured sarees carousel
    │   ├── stack/
    │   │   ├── WishlistStack.java         # CO3 — LIFO wishlist
    │   │   └── NavigationStack.java       # CO3 — Menu back-navigation
    │   ├── queue/
    │   │   ├── OrderQueue.java            # CO3 — Circular Queue for orders
    │   │   └── SareeDeque.java            # CO3 — Recently Viewed
    │   ├── heap/
    │   │   ├── MaxHeapDiscount.java       # CO3 — Top discounted sarees
    │   │   └── PopularityPriorityQueue.java # CO3 — Popular sarees
    │   └── hashtable/
    │       ├── SareeHashTableChaining.java      # CO4 — Saree catalogue
    │       └── UserHashTableOpenAddressing.java # CO4 — Authentication
    ├── algorithms/
    │   ├── search/
    │   │   └── SearchAlgorithms.java      # CO1 — Linear + Binary Search
    │   └── sort/
    │       └── SortingAlgorithms.java     # CO1 — All 5 sorting algorithms
    ├── auth/
    │   └── AuthManager.java              # CO4 — Login system
    ├── service/
    │   ├── CatalogService.java           # CO4, CO6 — Central data access
    │   ├── CartService.java              # CO3, CO5 — Cart + Billing
    │   └── WishlistService.java          # CO3, CO5 — Wishlist
    └── ui/
        ├── UserMenu.java                 # CO5, CO6 — Customer console
        └── AdminMenu.java                # CO6 — Admin console
```

---

## Data Structure → Feature Mapping (CO1–CO6)

### CO1 — Algorithm Analysis

| Algorithm       | Best Ω     | Average Θ  | Worst O    | Used For                          |
|-----------------|------------|------------|------------|-----------------------------------|
| Linear Search   | Ω(1)       | Θ(n)       | O(n)       | Keyword search (name/type/desc)   |
| Binary Search   | Ω(1)       | Θ(log n)   | O(log n)   | Price-range filter, ID lookup     |
| Bubble Sort     | Ω(n)       | Θ(n²)      | O(n²)      | Admin sort option (small n)       |
| Selection Sort  | Ω(n²)      | Θ(n²)      | O(n²)      | Admin sort, minimizes swaps       |
| Insertion Sort  | Ω(n)       | Θ(n²)      | O(n²)      | Admin sort (nearly-sorted data)   |
| Merge Sort      | Ω(n log n) | Θ(n log n) | O(n log n) | Stable admin sort, large n        |
| Quick Sort      | Ω(n log n) | Θ(n log n) | O(n²)      | General admin sort (default)      |

**Recurrence Relations:**
- Merge Sort: `T(n) = 2T(n/2) + O(n)` → **O(n log n)** by Master Theorem
- Binary Search: `T(n) = T(n/2) + O(1)` → **O(log n)**

### CO2 — Abstract Data Types

| Structure            | Operations Implemented               | Used For                    |
|----------------------|--------------------------------------|-----------------------------|
| Singly Linked List   | insert, delete, search, traverse, reverse, cycle-detect | Catalogue, hash chaining |
| Doubly Linked List   | insert (head/tail), delete, traverse (fwd/bwd) | Cart (O(1) tail insert) |
| Circular Linked List | insert, delete, traverse, cycle-detect | Featured sarees carousel |
| Array                | push, pop, peek, random-access       | WishlistStack, sorted views |

**Trade-off — Array vs Linked List:**
- Array: O(1) random access, O(n) insert/delete in middle, fixed size
- Linked List: O(1) insert/delete at known node, O(n) access, dynamic size

### CO3 — Stacks, Queues, Heap

| Structure          | O(enqueue/push) | O(dequeue/pop) | Used For                    |
|--------------------|-----------------|----------------|-----------------------------|
| WishlistStack      | O(1)            | O(1)           | Wishlist — LIFO view        |
| NavigationStack    | O(1)            | O(1)           | Back-button menu history    |
| OrderQueue (Circ.) | O(1)            | O(1)           | Order processing — FIFO     |
| SareeDeque         | O(1) both ends  | O(1) both ends | Recently Viewed (5-window)  |
| MaxHeapDiscount    | O(log n)        | O(log n)       | Top-5 discounted sarees     |
| PopularityPriorityQ| O(log n)        | O(log n)       | Top popular sarees          |

### CO4 — Hash-Based Structures

| Structure                    | Collision Strategy | Load Factor | Used For              |
|------------------------------|--------------------|-------------|-----------------------|
| SareeHashTableChaining       | Linked List chains | α < 1.0     | Saree CRUD + filter   |
| UserHashTableOpenAddressing  | Linear Probing     | α < 0.7     | User authentication   |
| Java HashMap                 | Built-in           | —           | Store/type grouping   |

### CO5 — Practical Applications

- **Cart**: DoublyLinkedList — insertAtTail O(1), per-item and combined billing with GST
- **Wishlist**: Array Stack — push O(1), LIFO view (most recently added first)
- **Orders**: Circular Queue — enqueue/dequeue O(1), FIFO order processing
- **Recently Viewed**: Deque — O(1) both ends, sliding window of 5 items

### CO6 — Integration Flow

```
Login → AuthManager (Open Addressing HT)
          ↓
     CatalogService (Chaining HT + LinkedList + SortedArrays)
          ↓
     UserMenu filters → findFiltered() → binarySearchPriceRange()
          ↓
     WishlistService (Stack) → CartService (DoublyLL)
          ↓
     placeOrder() → OrderQueue (CircularQueue)
          ↓
     generateBill() — per-item + combined + GST + shipping
```

---

## Billing Logic

When items are in the cart:

1. **First Saree Bill** — individual breakdown (base price, discount saving, GST, subtotal)
2. **Second Saree Bill** — same breakdown
3. **Combined Bill** — total items, total GST, shipping (free above ₹3,000), grand total

**GST Rates:**
- Silk, Wedding, Designer → 12% GST
- Cotton, Traditional → 5% GST

---

## Big-O Summary for Key Operations

| Operation                   | Time Complexity     | Data Structure Used          |
|-----------------------------|---------------------|------------------------------|
| Login authentication        | O(1) average        | Open Addressing Hash Table   |
| Find saree by ID            | O(1) average        | Chaining Hash Table          |
| Keyword search              | O(n)                | Linear Search over array     |
| Price range filter          | O(log n + k)        | Binary Search on sorted array|
| Filter by store             | O(n/k) average      | HashMap index                |
| Add to cart                 | O(1)                | Doubly Linked List tail      |
| Remove from cart            | O(n)                | Doubly LL search + O(1) delete|
| Add to wishlist             | O(1)                | Array Stack push             |
| Sort 24 sarees (quick sort) | O(n log n) ≈ 24×5  | Quick Sort (median-of-three) |
| Top-5 discounts             | O(k log n)          | Max-Heap extract-max         |
| Place order (n items)       | O(n)                | Circular Queue enqueue       |
| Generate bill               | O(n)                | DoublyLL traverse            |
