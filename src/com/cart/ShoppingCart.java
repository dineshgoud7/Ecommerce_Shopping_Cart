package com.cart;

import java.util.ArrayList;
import java.util.List;
import com.product.Product;
import com.product.ProductService;

public class ShoppingCart {
    private final List<CartItem> items = new ArrayList<>();

    public void addProduct(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.addQuantity(quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void viewCart() {
        System.out.println("\n===== Shopping Cart =====");
        if (items.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        double total = 0;
        for (CartItem item : items) {
            double subtotal = item.getSubtotal();
            total += subtotal;
            System.out.printf("%s | Quantity: %d | Subtotal: ₹%.2f%n",
                    item.getProduct().getName(), item.getQuantity(), subtotal);
        }
        System.out.printf("Total: ₹%.2f%n", total);
    }

    public boolean checkout(ProductService productService) {
        for (CartItem item : items) {
            Product current = productService.findById(item.getProduct().getId());
            if (current == null || current.getStock() < item.getQuantity()) {
                System.out.println("Insufficient stock for " + item.getProduct().getName());
                return false;
            }
        }

        List<CartItem> reduced = new ArrayList<>();
        for (CartItem item : items) {
            if (!productService.reduceStock(item.getProduct().getId(), item.getQuantity())) {
                // Best-effort rollback if a later stock update fails.
                for (CartItem previous : reduced) {
                    Product p = productService.findById(previous.getProduct().getId());
                    if (p != null) productService.updateStock(p.getId(), p.getStock() + previous.getQuantity());
                }
                return false;
            }
            reduced.add(item);
        }
        items.clear();
        return true;
    }
}
