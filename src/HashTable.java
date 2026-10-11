/**
 * Хеш-таблица (Hash Table) методом цепочек (Separate Chaining).
 * 
 * Принцип работы:
 * - Внутренний массив "корзин" (buckets).
 * - Хеш-функция преобразует ключ в индекс массива: index = hash(key) % capacity.
 * - При возникновении коллизий (два ключа дают один индекс) элементы сохраняются
 *   в связный список в этой ячейке.
 * - При превышении коэффициента заполнения (Load Factor, по умолчанию 0.75)
 *   таблица автоматически расширяется в 2 раза (Rehash) для сохранения O(1).
 * 
 * Временная сложность (Time Complexity):
 * - Вставка (Insert):
 *     Лучший случай: O(1) (нет коллизий, ячейка пуста).
 *     Худший случай: O(n) (все ключи попали в одну ячейку, или при расширении таблицы Rehash).
 *     Амортизированно / в среднем: O(1).
 * 
 * - Поиск (Search):
 *     Лучший случай: O(1) (ключ найден в самом начале цепочки).
 *     Худший случай: O(n) (все n элементов лежат в одной цепочке — коллизия всех ключей).
 *     В среднем: O(1).
 * 
 * - Удаление (Delete):
 *     Лучший случай: O(1) (первый элемент в цепочке).
 *     Худший случай: O(n) (все элементы в одной цепочке, и нужный в конце или отсутствует).
 *     В среднем: O(1).
 */
public class HashTable {

    public static class HashNode {
        public int key;
        public HashNode next;

        public HashNode(int key) {
            this.key = key;
            this.next = null;
        }
    }

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private HashNode[] buckets;
    private int capacity;
    private int size;
    private final float loadFactor;

    public HashTable() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int initialCapacity, float loadFactor) {
        this.capacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.buckets = new HashNode[capacity];
        this.size = 0;
    }

    /**
     * Хеш-функция для вычисления индекса корзины.
     * Используем битовое маскирование для предотвращения отрицательных индексов.
     */
    private int getBucketIndex(int key) {
        int hash = Integer.hashCode(key);
        // Дополнительное перемешивание битов для равномерного распределения
        hash ^= (hash >>> 16);
        return Math.floorMod(hash, capacity);
    }

    /**
     * Вставка ключа в хеш-таблицу.
     * Возвращает true, если ключ был добавлен, false если уже существовал.
     */
    public boolean insert(int key) {
        // Проверяем необходимость увеличения таблицы (Rehash)
        if ((float) (size + 1) / capacity > loadFactor) {
            rehash();
        }

        int bucketIndex = getBucketIndex(key);
        HashNode head = buckets[bucketIndex];

        // Проверяем, существует ли уже такой ключ (дубликаты не храним)
        HashNode current = head;
        while (current != null) {
            if (current.key == key) {
                return false; // Ключ уже есть
            }
            current = current.next;
        }

        // Вставляем новый узел в начало цепочки O(1)
        HashNode newNode = new HashNode(key);
        newNode.next = head;
        buckets[bucketIndex] = newNode;
        size++;
        return true;
    }

    /**
     * Поиск ключа в хеш-таблице.
     * Возвращает true, если ключ присутствует.
     */
    public boolean search(int key) {
        int bucketIndex = getBucketIndex(key);
        HashNode current = buckets[bucketIndex];

        while (current != null) {
            if (current.key == key) {
                return true; // Нашли за O(1) в среднем!
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Удаление ключа из хеш-таблицы.
     * Возвращает true, если ключ был удален, false если не найден.
     */
    public boolean delete(int key) {
        int bucketIndex = getBucketIndex(key);
        HashNode current = buckets[bucketIndex];
        HashNode prev = null;

        while (current != null) {
            if (current.key == key) {
                if (prev == null) {
                    // Удаляем голову цепочки
                    buckets[bucketIndex] = current.next;
                } else {
                    // Пропускаем текущий узел
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false; // Ключ не найден
    }

    /**
     * Динамическое перехеширование:
     * Удваиваем емкость массива и перераспределяем все элементы.
     */
    private void rehash() {
        int oldCapacity = capacity;
        HashNode[] oldBuckets = buckets;

        capacity = oldCapacity * 2;
        buckets = new HashNode[capacity];
        size = 0;

        for (int i = 0; i < oldCapacity; i++) {
            HashNode current = oldBuckets[i];
            while (current != null) {
                insert(current.key);
                current = current.next;
            }
        }
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public void clear() {
        buckets = new HashNode[DEFAULT_INITIAL_CAPACITY];
        capacity = DEFAULT_INITIAL_CAPACITY;
        size = 0;
    }

    /**
     * Возвращает максимальную длину цепочки (для анализа коллизий).
     */
    public int getMaxChainLength() {
        int max = 0;
        for (HashNode head : buckets) {
            int len = 0;
            HashNode cur = head;
            while (cur != null) {
                len++;
                cur = cur.next;
            }
            if (len > max) max = len;
        }
        return max;
    }
}
