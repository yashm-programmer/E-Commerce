package DataStructure;

import model.Product;

import java.util.ArrayList;
import java.util.List;

class Node {
    Product product;
    Node left, right;

    public Node(Product product) {
        this.product = product;
        left = right = null;
    }
}

public class BST {
    private Node root;

    public BST() {
        root = null;
    }

    public void insert(Product product) {
        root = insertRec(root, product);
    }

    private Node insertRec(Node root, Product product) {
        if (root == null) {
            root = new Node(product);
            return root;
        }

        if (product.getName().compareToIgnoreCase(root.product.getName()) < 0) {
            root.left = insertRec(root.left, product);
        } else if (product.getName().compareToIgnoreCase(root.product.getName()) > 0) {
            root.right = insertRec(root.right, product);
        } else {
            // Handle duplicate product names if necessary, for now we ignore them
        }

        return root;
    }

    public List<Product> searchByName(String name) {
        List<Product> results = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            return results; // Return empty list for empty search
        }
        String searchTerm = name.toLowerCase().trim();
        searchAllNodes(root, searchTerm, results);
        return results;
    }

    private void searchAllNodes(Node node, String searchTerm, List<Product> results) {
        if (node == null) {
            return;
        }

        // Check left subtree
        searchAllNodes(node.left, searchTerm, results);

        // Check current node
        if (node.product.getName().toLowerCase().contains(searchTerm)) {
            results.add(node.product);
        }

        // Check right subtree
        searchAllNodes(node.right, searchTerm, results);
    }
}
