import java.io.*;
import java.util.*;

public class AVL {
    static int iterationsCounter = 0;
    public static void main(String[] args) throws IOException {
        File file = new File("/Users/cisco/Desktop/Учеба /Прога /AADS/AVL Tree/untitled/src/file.txt");
        //fillFile(file);
        Random random = new Random();
        long startTime;
        AVLTree tree = new AVLTree();
        Scanner scanner = new Scanner(file);
        ArrayList<Integer> arrayWithValues = new ArrayList<>();

        // Подсчет времени вставки всех элементов в дерево и количества итераций
        while(scanner.hasNext()) {
            int value = scanner.nextInt();
            arrayWithValues.add(value);

           startTime = System.nanoTime();
           tree.insert(value);
            System.out.println("Время вставки элемента: " + (System.nanoTime() - startTime)
                    + ", количество итераций: " + iterationsCounter);
            iterationsCounter = 0;
        }

        System.out.println("\n--------------------------------------------------\n");

        // Подсчет времени поиска 100 случайных элементов
        for (int i = 0; i < 100; i++) {
            int value = arrayWithValues.get(random.nextInt(0, 10000));
            startTime = System.nanoTime();
            tree.find(value);
            System.out.println(" время поиска элемента " + value + " заняло: "
                    + (System.nanoTime() - startTime) + " итераций: " + iterationsCounter);
            iterationsCounter = 0;
        }

        System.out.println("\n--------------------------------------------------\n");

        //Подсчет времени удаления 1000 случайных значений
        for (int i = 0; i < 1000; i++) {
            int value = arrayWithValues.get(random.nextInt(0,10000));
            startTime = System.nanoTime();
            tree.remove(value);
            System.out.println("Время удаления элемента " + value + " заняло: "
                    + (System.nanoTime()-startTime) + " итераций: " + iterationsCounter);
            iterationsCounter = 0;
        }
    }

    public static class AVLTree {
        public static class Node {
            int balanceFactor;
            int value;
            int height;
            Node left;
            Node right;

            public Node(int value) {
                this.value = value;
            }
        }

        private Node root;
        private int nodeCount;

        public int size() {
            return nodeCount;
        }

        public boolean contains(int value) {
            return contains(root, value);
        }

        private boolean contains(Node node, int value) {
            if (node == null) return false;

            if (value > node.value) {
                return contains(node.right, value);
            }
            if (value < node.value) {
                return contains(node.left, value);
            }
            return true;
        }

        public Node find(int value) {
            Node iterator = root;
            while (value < iterator.value && iterator.left != null
                    || value > iterator.value && iterator.right != null) {
                iterator = value > iterator.value ? iterator.right : iterator.left;
                iterationsCounter++;
            }

            return iterator.value == value ? iterator : null;
        }

        public boolean insert(int value) {
            if (!contains(root, value)) {
                root = insert(root, value);
                nodeCount++;
                return true;
            }
            return false;
        }

        private Node insert(Node node, int value) {
            iterationsCounter++;
            if (node == null) return new Node(value);
            if (value < node.value) {
                node.left = insert(node.left, value);
            } else {
                node.right = insert(node.right, value);
            }
            update(node);
            return balance(node);
        }

        private void update(Node node) {
            iterationsCounter++;
            int leftNodeHeight = (node.left == null) ? -1 : node.left.height;
            int rightNodeHeight = (node.right == null) ? -1 : node.right.height;

            node.height = 1 + Math.max(leftNodeHeight, rightNodeHeight);
            node.balanceFactor = rightNodeHeight - leftNodeHeight;
        }

        private Node balance(Node node) {
            iterationsCounter++;
            if (node.balanceFactor == -2) {
                if (node.left.balanceFactor <= 0) {
                    return leftLeftCase(node);
                } else {
                    return leftRightCase(node);
                }
            } else if (node.balanceFactor == 2) {
                if (node.right.balanceFactor >= 0) {
                    return rightRightCase(node);
                } else {
                    return rightLeftCase(node);
                }
            }
            return node;
        }

        private Node leftLeftCase(Node node) {
            return rightRotation(node);
        }

        private Node leftRightCase(Node node) {
            node.left = leftRotation(node.left);
            return leftLeftCase(node);
        }

        private Node rightRightCase(Node node) {
            return leftRotation(node);
        }

        private Node rightLeftCase(Node node) {
            node.right = rightRotation(node.right);
            return rightRightCase(node);
        }

        private Node leftRotation(Node node) {
            Node newParent = node.right;
            node.right = newParent.left;
            newParent.left = node;
            update(node);
            update(newParent);
            return newParent;
        }

        private Node rightRotation(Node node) {
            Node newParent = node.left;
            node.left = newParent.right;
            newParent.right = node;
            update(node);
            update(newParent);
            return newParent;
        }

        public boolean remove(int elem) {
            if (contains(root, elem)) {
                root = remove(root, elem);
                nodeCount--;
                return true;
            }
            return false;
        }

        private Node remove(Node node, int elem) {
            if (node == null) return null;
            iterationsCounter++;
            if (elem < node.value) {
                node.left = remove(node.left, elem);
            } else if (elem > node.value) {
                node.right = remove(node.right, elem);
            } else {
                if (node.left == null) {
                    return node.right;
                } else if (node.right == null) {
                    return node.left;
                } else {
                    if (node.left.height > node.right.height) {
                        int successorValue = findMax(node.left);
                        node.value = successorValue;
                        node.left = remove(node.left, successorValue);
                    } else {
                        int successorValue = findMin(node.right);
                        node.value = successorValue;
                        node.right = remove(node.right, successorValue);
                    }
                }
            }
            update(node);
            return balance(node);
        }

        private int findMin(Node node) {
            while (node.left != null) {
                node = node.left;
                iterationsCounter++;
            }
            return node.value;
        }

        private int findMax(Node node) {
            while (node.right != null) {
                node = node.right;
                iterationsCounter++;
            }
            return node.value;
        }

        public void printTree() {
            Queue<Node> queue = new ArrayDeque<>();
            int currentLevelNodes = 1;
            int nextLevelNodes = 0;
            queue.add(this.root);
            while (!queue.isEmpty()) {
                Node node = queue.remove();
                System.out.print(node.value + " ");
                currentLevelNodes--;
                if (node.left != null) {
                    queue.add(node.left);
                    nextLevelNodes++;
                }
                if (node.right != null) {
                    queue.add(node.right);
                    nextLevelNodes++;
                }
                if (currentLevelNodes == 0) {
                    System.out.println(("\n"));
                    currentLevelNodes = nextLevelNodes;
                    nextLevelNodes = 0;
                }
            }
        }
    }

    public static void fillFile(File file) throws IOException {
        Random r = new Random();
        BufferedWriter bf = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < 10000; i++) {
            bf.write(r.nextInt() + " ");
            bf.flush();
        }
    }
}
