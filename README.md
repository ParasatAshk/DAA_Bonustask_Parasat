# Binary Search Tree (BST) vs Hash Table Performance Analysis

## Overview
Comparative performance analysis and implementation of a Binary Search Tree (BST) and a Hash Table (Separate Chaining) in Java for 100, 1,000, and 10,000 elements.

## Project Structure
- `src/BinarySearchTree.java`: Custom BST implementation (insert, search, delete, height).
- `src/HashTable.java`: Custom Hash Table with separate chaining and dynamic resizing (load factor = 0.75).
- `src/PerformanceComparison.java`: Benchmark suite measuring insertion, search hits/misses, and deletion.
- `src/DataStructuresTest.java`: Verification tests for both data structures.
- `REPORT.md`: Hand-written analysis report.

## Time Complexity Comparison

| Data Structure | Operation | Best Case | Average Case | Worst Case | Worst-Case Scenario |
| :--- | :--- | :---: | :---: | :---: | :--- |
| **BST** | **Insert** | $O(1)$ | $O(\log n)$ | $O(n)$ | Pre-sorted or reverse-sorted input (tree degenerates into a linked list). |
| | **Search** | $O(1)$ | $O(\log n)$ | $O(n)$ | Target element is at the leaf of a degenerate tree. |
| | **Delete** | $O(1)$ | $O(\log n)$ | $O(n)$ | Target element requires traversing full height of degenerate tree. |
| **Hash Table** | **Insert** | $O(1)$ | $O(1)$ | $O(n)$ | Table resize (rehashing all $n$ items) or extreme hash collision. |
| | **Search** | $O(1)$ | $O(1)$ | $O(n)$ | All elements collide into the same bucket. |
| | **Delete** | $O(1)$ | $O(1)$ | $O(n)$ | Traversing a fully collided chain of size $n$. |

## Benchmark Results

Tested on Apple Silicon / OpenJDK 25 with JVM warm-up and 5-run averaging:

| $N$ | Operation | BST (ms) | Hash Table (ms) | Comparison |
| :--- | :--- | :---: | :---: | :--- |
| **100** | Insert | 0.0112 ms | 0.0172 ms | BST is 1.5x faster |
| | Search (Hit) | 0.0064 ms | 0.0020 ms | **Hash Table is 3.2x faster** |
| | Search (Miss) | 0.0040 ms | 0.0023 ms | **Hash Table is 1.7x faster** |
| | Delete | 0.0070 ms | 0.0021 ms | **Hash Table is 3.4x faster** |
| **1,000** | Insert | 0.0632 ms | 0.0884 ms | BST is 1.4x faster |
| | Search (Hit) | 0.0537 ms | 0.0160 ms | **Hash Table is 3.4x faster** |
| | Search (Miss) | 0.0200 ms | 0.0210 ms | Comparable |
| | Delete | 0.0631 ms | 0.0178 ms | **Hash Table is 3.5x faster** |
| **10,000** | Insert | 0.6646 ms | 0.3735 ms | **Hash Table is 1.8x faster** |
| | Search (Hit) | 0.5942 ms | 0.0907 ms | **Hash Table is 6.6x faster** |
| | Search (Miss) | 0.1038 ms | 0.1214 ms | BST is 1.2x faster |
| | Delete | 0.6814 ms | 0.0937 ms | **Hash Table is 7.3x faster** |

### Worst-Case Test (Sorted Input, $N = 2,000$)
- **BST Height:** 2,000 (degenerate single-branch tree).
- **BST Insertion Time:** 3.207 ms
- **Hash Table Insertion Time:** 0.047 ms (**Hash Table is 68.8x faster**)
- **1,000 Searches for Worst Key:** BST: 2.855 ms vs Hash Table: 0.012 ms (**Hash Table is 248.3x faster**)

## Why Performance Differs in Practice

1. **Cache Locality:** Hash Table uses a contiguous array for bucket references, utilizing CPU L1/L2 caches effectively. BST nodes are scattered in the heap, causing pointer chasing and cache misses.
2. **Branch Prediction:** BST performs conditional branching (`key < node.key`) at every level, increasing branch mispredictions. Hash Table calculates bucket index with direct bitwise hashing.
3. **Rehash Overhead:** For smaller sizes ($N \le 1,000$), BST insert is faster because Hash Table pays resizing and array allocation costs. At scale ($N \ge 10,000$), $O(1)$ dominates.
4. **Input Sensitivity:** Unbalanced BST degrades to $O(n^2)$ total insert time on sorted input. Hash Table retains $O(1)$ regardless of insertion order.

## Build and Run

```bash
# Compile
javac src/*.java

# Run Unit Tests
java -ea -cp src DataStructuresTest

# Run Benchmark
java -cp src PerformanceComparison
```
