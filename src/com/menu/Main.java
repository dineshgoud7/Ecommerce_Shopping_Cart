package com.menu;

import java.util.Scanner;
import com.user.User;
import com.user.UserService;
import com.product.Product;
import com.product.ProductService;
import com.cart.ShoppingCart;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final ProductService productService = new ProductService();
    private static User loggedInUser;
    private static ShoppingCart cart;

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== E-COMMERCE SHOPPING CART =====");
            System.out.println("1. User Registration");
            System.out.println("2. User Login");
            System.out.println("3. Browse Products");
            System.out.println("4. Add Product to Cart");
            System.out.println("5. View Cart");
            System.out.println("6. Checkout");
            System.out.println("7. Admin Operations");
            System.out.println("8. Logout");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            Integer option = readInt();
            if (option == null) continue;

            switch (option) {
                case 1 -> userService.registerUser(sc);
                case 2 -> login();
                case 3 -> productService.viewProducts();
                case 4 -> addToCart();
                case 5 -> viewCart();
                case 6 -> checkout();
                case 7 -> adminMenu();
                case 8 -> logout();
                case 9 -> running = false;
                default -> System.out.println("Invalid option. Choose 1-9.");
            }
        }
        System.out.println("Thank you for using the shopping cart.");
        sc.close();
    }

    private static Integer readInt() {
        String value = sc.nextLine().trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }

    private static void login() {
        if (loggedInUser != null) {
            System.out.println("Already logged in as " + loggedInUser.getUsername());
            return;
        }
        loggedInUser = userService.loginUser(sc);
        if (loggedInUser != null) cart = new ShoppingCart();
    }

    private static void logout() {
        if (loggedInUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        loggedInUser = null;
        cart = null;
        System.out.println("Logged out successfully.");
    }

    private static void addToCart() {
        if (!requireLogin()) return;
        productService.viewProducts();
        System.out.print("Enter product ID: ");
        Integer id = readInt();
        if (id == null) return;
        Product product = productService.findById(id);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }
        System.out.print("Enter quantity: ");
        Integer quantity = readInt();
        if (quantity == null || quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return;
        }
        if (quantity > product.getStock()) {
            System.out.println("Not enough stock available.");
            return;
        }
        cart.addProduct(product, quantity);
        System.out.println("Product added to cart.");
    }

    private static void viewCart() {
        if (!requireLogin()) return;
        cart.viewCart();
    }

    private static void checkout() {
        if (!requireLogin()) return;
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        cart.viewCart();
        System.out.print("Proceed with checkout? (Y/N): ");
        String answer = sc.nextLine().trim();
        if (!answer.equalsIgnoreCase("Y")) {
            System.out.println("Checkout cancelled.");
            return;
        }
        if (cart.checkout(productService)) {
            System.out.println("Order placed successfully. Thank you!");
            cart = new ShoppingCart();
        } else {
            System.out.println("Checkout failed. Please review stock and try again.");
        }
    }

    private static boolean requireLogin() {
        if (loggedInUser == null) {
            System.out.println("Please log in first.");
            return false;
        }
        return true;
    }

    private static void adminMenu() {
        System.out.print("Enter admin password: ");
        String password = sc.nextLine();
        if (!password.equals("admin123")) {
            System.out.println("Invalid admin password.");
            return;
        }
        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println("\n===== ADMIN OPERATIONS =====");
            System.out.println("1. View Products");
            System.out.println("2. Add Product");
            System.out.println("3. Update Product Stock");
            System.out.println("4. View Registered Users");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");
            Integer option = readInt();
            if (option == null) continue;
            switch (option) {
                case 1 -> productService.viewProducts();
                case 2 -> addProduct();
                case 3 -> updateStock();
                case 4 -> userService.viewUsers();
                case 5 -> inAdmin = false;
                default -> System.out.println("Invalid admin option.");
            }
        }
    }

    private static void addProduct() {
        System.out.print("Product name: ");
        String name = sc.nextLine().trim();
        System.out.print("Description: ");
        String description = sc.nextLine().trim();
        System.out.print("Price: ");
        double price = readDouble();
        System.out.print("Stock quantity: ");
        Integer stock = readInt();
        if (name.isEmpty() || description.isEmpty() || price <= 0 || stock == null || stock < 0) {
            System.out.println("Invalid product details.");
            return;
        }
        productService.addProduct(name, description, price, stock);
    }

    private static void updateStock() {
        productService.viewProducts();
        System.out.print("Product ID: ");
        Integer id = readInt();
        if (id == null) return;
        System.out.print("New stock quantity: ");
        Integer stock = readInt();
        if (stock == null || stock < 0) {
            System.out.println("Stock cannot be negative.");
            return;
        }
        productService.updateStock(id, stock);
    }

    private static double readDouble() {
        String value = sc.nextLine().trim();
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
            return -1;
        }
    }
}
