public class DataStructuresTest {
    public static void main(String[] args) {
        testBST();
        testHashTable();
        System.out.println("✅ Все тесты корректности пройдены успешно!");
    }

    private static void testBST() {
        System.out.println("--- Тестирование BST ---");
        BinarySearchTree bst = new BinarySearchTree();

        // 1. Вставка
        int[] keys = {50, 30, 70, 20, 40, 60, 80};
        for (int k : keys) {
            bst.insert(k);
        }
        assert bst.size() == 7 : "Размер BST должен быть 7";

        // Повторная вставка дубликата не должна увеличивать размер
        assert !bst.insert(50) : "Дубликат не должен вставляться";
        assert bst.size() == 7 : "Размер должен остаться 7";

        // 2. Поиск
        for (int k : keys) {
            assert bst.search(k) : "Ключ " + k + " должен быть найден в BST";
        }
        assert !bst.search(100) : "Ключ 100 не должен быть найден в BST";
        assert !bst.search(10) : "Ключ 10 не должен быть найден в BST";

        // 3. Удаление листа (20)
        assert bst.delete(20) : "Удаление листа должно вернуть true";
        assert !bst.search(20) : "20 не должен быть найден";
        assert bst.size() == 6 : "Размер должен быть 6";

        // 4. Удаление узла с одним потомком (30 теперь имеет только правого потомка 40)
        assert bst.delete(30) : "Удаление узла с 1 потомком должно вернуть true";
        assert !bst.search(30) : "30 не должен быть найден";
        assert bst.search(40) : "40 должен остаться доступен";
        assert bst.size() == 5 : "Размер должен быть 5";

        // 5. Удаление узла с двумя потомками (корень 50)
        assert bst.delete(50) : "Удаление корня с 2 потомками должно вернуть true";
        assert !bst.search(50) : "50 не должен быть найден";
        assert bst.search(60) : "60 должен остаться";
        assert bst.search(70) : "70 должен остаться";
        assert bst.search(80) : "80 должен остаться";
        assert bst.search(40) : "40 должен остаться";
        assert bst.size() == 4 : "Размер должен быть 4";

        System.out.println("BST: Пройдены все проверки (вставка, поиск, удаление 3 видов узлов).");
    }

    private static void testHashTable() {
        System.out.println("--- Тестирование Hash Table ---");
        HashTable table = new HashTable(4, 0.75f); // маленькая таблица для проверки рехеширования

        int[] keys = {1, 5, 9, 13, 17, 2, 6, 10, 14, 18, 22};
        for (int k : keys) {
            table.insert(k);
        }
        assert table.size() == keys.length : "Размер должен соответствовать числу элементов";
        assert !table.insert(1) : "Дубликат не должен вставляться";

        // Проверка поиска
        for (int k : keys) {
            assert table.search(k) : "Ключ " + k + " должен быть найден в Hash Table";
        }
        assert !table.search(999) : "Ключ 999 не должен быть найден";

        // Проверка удаления
        assert table.delete(5) : "Удаление 5 должно вернуть true";
        assert !table.search(5) : "5 не должен быть найден";
        assert table.size() == keys.length - 1 : "Размер должен уменьшиться на 1";
        assert !table.delete(5) : "Повторное удаление 5 должно вернуть false";

        assert table.delete(1) : "Удаление головы цепочки должно работать";
        assert !table.search(1) : "1 не должен быть найден";

        System.out.println("Hash Table: Пройдены все проверки (коллизии, расширение, удаление).");
    }
}
