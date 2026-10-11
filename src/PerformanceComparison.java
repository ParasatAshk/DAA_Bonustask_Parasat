import java.util.*;

/**
 * Сравнительный анализ производительности Binary Search Tree (BST) и Hash Table.
 * 
 * Тестируемые размеры данных:
 * - 100 элементов
 * - 1 000 элементов
 * - 10 000 элементов
 * 
 * Измеряемые операции:
 * 1. Вставка (Insertion)
 * 2. Поиск существующих ключей (Search Hits)
 * 3. Поиск отсутствующих ключей (Search Misses)
 * 4. Удаление (Deletion)
 */
public class PerformanceComparison {

    private static final int ITERATIONS = 5; // Усреднение по 5 прогонам для стабильности результатов

    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("     СРАВНЕНИЕ ПРОИЗВОДИТЕЛЬНОСТИ: BINARY SEARCH TREE (BST) vs HASH TABLE (JAVA)        ");
        System.out.println("==========================================================================================\n");

        // 1. Прогрев JVM (Warm-up), чтобы JIT-компилятор скомпилировал байт-код в машинный код
        System.out.print("Выполняется прогрев JVM (Warm-up)... ");
        warmUp();
        System.out.println("Готово!\n");

        int[] testSizes = {100, 1_000, 10_000};

        // Результаты для итоговой таблицы
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-15s | %-15s | %-15s | %-15s%n",
                "Размер N", "Операция", "BST (мс)", "Hash Table (мс)", "Кто быстрее / Во сколько");
        System.out.println("------------------------------------------------------------------------------------------");

        for (int n : testSizes) {
            runBenchmarkForSize(n);
            System.out.println("------------------------------------------------------------------------------------------");
        }

        // Демонстрация худшего случая (Worst-Case) на отсортированных данных
        runWorstCaseComparison(2_000);

        printTheoreticalSummary();
    }

    /**
     * Запуск тестов для конкретного размера N со случайными ключами.
     */
    private static void runBenchmarkForSize(int n) {
        double bstTotalInsert = 0, htTotalInsert = 0;
        double bstTotalSearchHit = 0, htTotalSearchHit = 0;
        double bstTotalSearchMiss = 0, htTotalSearchMiss = 0;
        double bstTotalDelete = 0, htTotalDelete = 0;

        int bstHeight = 0;
        int htMaxChain = 0;

        Random rnd = new Random(42);

        for (int it = 0; it < ITERATIONS; it++) {
            // Генерируем N уникальных случайных ключей для вставки
            Set<Integer> uniqueKeys = new LinkedHashSet<>(n);
            while (uniqueKeys.size() < n) {
                uniqueKeys.add(rnd.nextInt(n * 10) + 1);
            }
            int[] insertKeys = uniqueKeys.stream().mapToInt(Integer::intValue).toArray();

            // Ключи, которых точно нет (отрицательные числа) для поиска промахов
            int[] missingKeys = new int[n];
            for (int i = 0; i < n; i++) {
                missingKeys[i] = -(i + 1);
            }

            // --- ТЕСТ BST ---
            BinarySearchTree bst = new BinarySearchTree();

            // Вставка
            long t0 = System.nanoTime();
            for (int k : insertKeys) bst.insert(k);
            long t1 = System.nanoTime();
            bstTotalInsert += (t1 - t0) / 1_000_000.0;
            bstHeight = bst.height();

            // Поиск существующих (Hit)
            t0 = System.nanoTime();
            for (int k : insertKeys) bst.search(k);
            t1 = System.nanoTime();
            bstTotalSearchHit += (t1 - t0) / 1_000_000.0;

            // Поиск отсутствующих (Miss)
            t0 = System.nanoTime();
            for (int k : missingKeys) bst.search(k);
            t1 = System.nanoTime();
            bstTotalSearchMiss += (t1 - t0) / 1_000_000.0;

            // Удаление
            t0 = System.nanoTime();
            for (int k : insertKeys) bst.delete(k);
            t1 = System.nanoTime();
            bstTotalDelete += (t1 - t0) / 1_000_000.0;

            // --- ТЕСТ HASH TABLE ---
            HashTable ht = new HashTable();

            // Вставка
            t0 = System.nanoTime();
            for (int k : insertKeys) ht.insert(k);
            t1 = System.nanoTime();
            htTotalInsert += (t1 - t0) / 1_000_000.0;
            htMaxChain = ht.getMaxChainLength();

            // Поиск существующих (Hit)
            t0 = System.nanoTime();
            for (int k : insertKeys) ht.search(k);
            t1 = System.nanoTime();
            htTotalSearchHit += (t1 - t0) / 1_000_000.0;

            // Поиск отсутствующих (Miss)
            t0 = System.nanoTime();
            for (int k : missingKeys) ht.search(k);
            t1 = System.nanoTime();
            htTotalSearchMiss += (t1 - t0) / 1_000_000.0;

            // Удаление
            t0 = System.nanoTime();
            for (int k : insertKeys) ht.delete(k);
            t1 = System.nanoTime();
            htTotalDelete += (t1 - t0) / 1_000_000.0;
        }

        double bstAvgInsert = bstTotalInsert / ITERATIONS;
        double htAvgInsert = htTotalInsert / ITERATIONS;

        double bstAvgSearchHit = bstTotalSearchHit / ITERATIONS;
        double htAvgSearchHit = htTotalSearchHit / ITERATIONS;

        double bstAvgSearchMiss = bstTotalSearchMiss / ITERATIONS;
        double htAvgSearchMiss = htTotalSearchMiss / ITERATIONS;

        double bstAvgDelete = bstTotalDelete / ITERATIONS;
        double htAvgDelete = htTotalDelete / ITERATIONS;

        printRow(String.valueOf(n), "Вставка (Insert)", bstAvgInsert, htAvgInsert);
        printRow("", "Поиск (Hit)", bstAvgSearchHit, htAvgSearchHit);
        printRow("", "Поиск (Miss)", bstAvgSearchMiss, htAvgSearchMiss);
        printRow("", "Удаление (Delete)", bstAvgDelete, htAvgDelete);

        System.out.printf("   [Детали N=%d]: Высота BST = %d (теор. log2(N) ≈ %.1f) | Hash Table макс. цепочка = %d%n",
                n, bstHeight, (Math.log(n) / Math.log(2)), htMaxChain);
    }

    private static void printRow(String nStr, String op, double bstTime, double htTime) {
        String ratio;
        if (bstTime < 0.0001 && htTime < 0.0001) {
            ratio = "Одинаково быстро (~0 мс)";
        } else if (htTime < bstTime) {
            double speedup = (htTime == 0) ? bstTime * 1000 : (bstTime / htTime);
            ratio = String.format("Hash Table быстрее в %.1fx", speedup);
        } else {
            double speedup = (bstTime == 0) ? htTime * 1000 : (htTime / bstTime);
            ratio = String.format("BST быстрее в %.1fx", speedup);
        }

        System.out.printf("%-10s | %-15s | %12.4f мс | %12.4f мс | %s%n",
                nStr, op, bstTime, htTime, ratio);
    }

    /**
     * Демонстрация ХУДШЕГО случая:
     * Подаем отсортированные данные: 1, 2, 3, 4, ... N.
     * BST вырождается в связный список высотой N (O(N^2) суммарно)!
     * Hash Table по-прежнему работает за O(1) в среднем!
     */
    private static void runWorstCaseComparison(int n) {
        System.out.println("\n==========================================================================================");
        System.out.println("  ДЕМОНСТРАЦИЯ ХУДШЕГО СЛУЧАЯ (WORST-CASE): Вставка отсортированных данных (1, 2, ... " + n + ")");
        System.out.println("==========================================================================================");

        BinarySearchTree bst = new BinarySearchTree();
        HashTable ht = new HashTable();

        // 1. Вставка в BST отсортированных данных
        long t0 = System.nanoTime();
        for (int i = 1; i <= n; i++) bst.insert(i);
        long t1 = System.nanoTime();
        double bstInsertTime = (t1 - t0) / 1_000_000.0;

        // 2. Вставка в Hash Table отсортированных данных
        t0 = System.nanoTime();
        for (int i = 1; i <= n; i++) ht.insert(i);
        t1 = System.nanoTime();
        double htInsertTime = (t1 - t0) / 1_000_000.0;

        // 3. Поиск последнего элемента (худший случай)
        t0 = System.nanoTime();
        for (int i = 0; i < 1000; i++) bst.search(n);
        t1 = System.nanoTime();
        double bstSearchTime = (t1 - t0) / 1_000_000.0;

        t0 = System.nanoTime();
        for (int i = 0; i < 1000; i++) ht.search(n);
        t1 = System.nanoTime();
        double htSearchTime = (t1 - t0) / 1_000_000.0;

        System.out.printf("BST: Высота дерева = %d (выродилось в цепочку! O(n))%n", bst.height());
        System.out.printf("Вставка %d чисел: BST = %.3f мс  vs  Hash Table = %.3f мс (Hash Table быстрее в %.1fx!)%n",
                n, bstInsertTime, htInsertTime, (bstInsertTime / Math.max(htInsertTime, 0.0001)));
        System.out.printf("1000 поисков худшего ключа: BST = %.3f мс  vs  Hash Table = %.3f мс (Hash Table быстрее в %.1fx!)%n",
                bstSearchTime, htSearchTime, (bstSearchTime / Math.max(htSearchTime, 0.0001)));
        System.out.println("==========================================================================================\n");
    }

    /**
     * Прогрев JVM для исключения влияния компиляции и сборщика мусора.
     */
    private static void warmUp() {
        BinarySearchTree bst = new BinarySearchTree();
        HashTable ht = new HashTable();
        for (int i = 0; i < 5_000; i++) {
            bst.insert(i);
            ht.insert(i);
        }
        for (int i = 0; i < 5_000; i++) {
            bst.search(i);
            ht.search(i);
        }
        for (int i = 0; i < 5_000; i++) {
            bst.delete(i);
            ht.delete(i);
        }
    }

    private static void printTheoreticalSummary() {
        System.out.println("==========================================================================================");
        System.out.println("                        ТЕОРЕТИЧЕСКАЯ ТАБЛИЦА СЛОЖНОСТИ                                   ");
        System.out.println("==========================================================================================");
        System.out.println("| Структура данных | Операция | Лучший случай | Средний случай | Худший случай | Память  |");
        System.out.println("|------------------|----------|---------------|----------------|---------------|---------|");
        System.out.println("| BST              | Insert   | O(1)          | O(log n)       | O(n)          | O(n)    |");
        System.out.println("| BST              | Search   | O(1)          | O(log n)       | O(n)          | O(n)    |");
        System.out.println("| BST              | Delete   | O(1)          | O(log n)       | O(n)          | O(n)    |");
        System.out.println("| Hash Table       | Insert   | O(1)          | O(1)           | O(n)*         | O(n)    |");
        System.out.println("| Hash Table       | Search   | O(1)          | O(1)           | O(n)          | O(n)    |");
        System.out.println("| Hash Table       | Delete   | O(1)          | O(1)           | O(n)          | O(n)    |");
        System.out.println("==========================================================================================");
        System.out.println("* В Hash Table худший случай вставки возникает при переполнении и расширении (Rehash),   ");
        System.out.println("  либо если абсолютно все ключи имеют одинаковый хеш (катастрофическая коллизия).        ");
        System.out.println("==========================================================================================");
    }
}
