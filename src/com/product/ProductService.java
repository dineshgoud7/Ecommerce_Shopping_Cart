package com.product;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private final List<Product> products = new ArrayList<>();
    private int nextId = 1;
    private final Path filePath = Path.of("data", "products.txt");

    public ProductService() {
        loadProducts();
        if (products.isEmpty()) {
            addProduct("Wireless Mouse", "Wireless computer mouse", 799.00, 20);
            addProduct("Keyboard", "USB keyboard", 999.00, 15);
            addProduct("Headphones", "Over-ear headphones", 1499.00, 10);
        }
    }

    public void viewProducts() {
        System.out.println("\n===== Products =====");
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        for (Product product : products) System.out.println(product);
    }

    public Product findById(int id) {
        for (Product product : products) if (product.getId() == id) return product;
        return null;
    }

    public void addProduct(String name, String description, double price, int stock) {
        if (name.contains("|") || description.contains("|")) {
            System.out.println("Product fields cannot contain |.");
            return;
        }
        Product product = new Product(nextId, name, description, price, stock);
        products.add(product);
        if (saveProducts()) {
            nextId++;
            System.out.println("Product saved with ID " + product.getId() + ".");
        } else {
            products.remove(product);
            System.out.println("Could not save product.");
        }
    }

    public boolean updateStock(int id, int newStock) {
        Product product = findById(id);
        if (product == null) {
            System.out.println("Product not found.");
            return false;
        }
        int oldStock = product.getStock();
        product.setStock(newStock);
        if (saveProducts()) {
            System.out.println("Stock updated.");
            return true;
        }
        product.setStock(oldStock);
        System.out.println("Could not save stock update.");
        return false;
    }

    public boolean reduceStock(int id, int quantity) {
        Product product = findById(id);
        if (product == null || quantity <= 0 || product.getStock() < quantity) return false;
        int oldStock = product.getStock();
        product.setStock(oldStock - quantity);
        if (saveProducts()) return true;
        product.setStock(oldStock);
        return false;
    }

    private boolean saveProducts() {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = new ArrayList<>();
            for (Product p : products) {
                lines.add(p.getId() + "|" + p.getName() + "|" + p.getDescription()
                        + "|" + p.getPrice() + "|" + p.getStock());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving products: " + e.getMessage());
            return false;
        }
    }

    private void loadProducts() {
        try {
            Files.createDirectories(filePath.getParent());
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                return;
            }
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                String[] data = line.split("\\|", -1);
                if (data.length != 5) continue;
                try {
                    int id = Integer.parseInt(data[0]);
                    double price = Double.parseDouble(data[3]);
                    int stock = Integer.parseInt(data[4]);
                    products.add(new Product(id, data[1], data[2], price, stock));
                    nextId = Math.max(nextId, id + 1);
                } catch (NumberFormatException ignored) {
                    System.out.println("Skipping invalid product record.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }
}
