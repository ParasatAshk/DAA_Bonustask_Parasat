/**
 * Двоичное дерево поиска (Binary Search Tree - BST).
 * 
 * Основное свойство BST:
 * Для любого узла X:
 * - Все ключи в левом поддереве < X.key
 * - Все ключи в правом поддереве > X.key
 * 
 * Временная сложность (Time Complexity):
 * - Вставка (Insert):
 *     Лучший случай: O(1) (если вставляем у корня) или O(log n) для сбалансированного дерева.
 *     Худший случай: O(n) (если элементы вставляются по порядку: 1, 2, 3..., дерево вырождается в список).
 *     В среднем: O(log n).
 * 
 * - Поиск (Search):
 *     Лучший случай: O(1) (искомый элемент находится прямо в корне).
 *     Худший случай: O(n) (дерево вырождено в список или элемент на самом дне / отсутствует).
 *     В среднем: O(log n).
 * 
 * - Удаление (Delete):
 *     Лучший случай: O(1) (удаляемый элемент в корне и не имеет потомков).
 *     Худший случай: O(n) (дерево вырождено в список).
 *     В среднем: O(log n).
 */
public class BinarySearchTree {

    public static class Node {
        public int key;
        public Node left;
        public Node right;

        public Node(int key) {
            this.key = key;
        }
    }

    private Node root;
    private int size;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Вставка нового ключа в BST.
     * Если ключ уже существует, дубликаты игнорируются.
     */
    public boolean insert(int key) {
        int initialSize = size;
        root = insertRecursive(root, key);
        return size > initialSize;
    }

    private Node insertRecursive(Node current, int key) {
        if (current == null) {
            size++;
            return new Node(key);
        }

        if (key < current.key) {
            current.left = insertRecursive(current.left, key);
        } else if (key > current.key) {
            current.right = insertRecursive(current.right, key);
        }
        // Если key == current.key, дубликат не вставляем
        return current;
    }

    /**
     * Поиск ключа в BST.
     * Возвращает true, если ключ найден, иначе false.
     */
    public boolean search(int key) {
        Node current = root;
        while (current != null) {
            if (key == current.key) {
                return true; // Нашли! O(1) если прямо в корне
            }
            if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false; // Ключ не найден
    }

    /**
     * Удаление ключа из BST.
     * Возвращает true, если ключ был удален, false если ключ не найден.
     */
    public boolean delete(int key) {
        int initialSize = size;
        root = deleteRecursive(root, key);
        return size < initialSize;
    }

    private Node deleteRecursive(Node current, int key) {
        if (current == null) {
            return null;
        }

        if (key < current.key) {
            current.left = deleteRecursive(current.left, key);
        } else if (key > current.key) {
            current.right = deleteRecursive(current.right, key);
        } else {
            // Узел найден! Обрабатываем 3 случая:
            size--;

            // Случай 1: Узел не имеет детей (лист)
            if (current.left == null && current.right == null) {
                return null;
            }

            // Случай 2: Узел имеет только одного потомка
            if (current.left == null) {
                return current.right;
            }
            if (current.right == null) {
                return current.left;
            }

            // Случай 3: Узел имеет двух потомков.
            // Ищем наименьший элемент в правом поддереве (in-order successor)
            int smallestValue = findMin(current.right);
            current.key = smallestValue;
            // Увеличиваем размер обратно временно, так как рекурсивный вызов уменьшит его
            size++;
            current.right = deleteRecursive(current.right, smallestValue);
        }
        return current;
    }

    private int findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node.key;
    }

    public int size() {
        return size;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Вычисление высоты дерева (показывает степень сбалансированности).
     */
    public int height() {
        return heightRecursive(root);
    }

    private int heightRecursive(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(heightRecursive(node.left), heightRecursive(node.right));
    }
}
