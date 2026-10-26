package com.user;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserService {
    private final List<User> users = new ArrayList<>();
    private int nextId = 1;
    private final Path filePath = Path.of("data", "users.txt");

    public UserService() {
        loadUsers();
    }

    public void registerUser(Scanner sc) {
        System.out.println("\n===== User Registration =====");
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.print("Gender (M/F/O): ");
        String gender = sc.nextLine().trim().toUpperCase();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Address: ");
        String address = sc.nextLine().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || address.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }
        if (username.contains("|") || password.contains("|") || email.contains("|") || address.contains("|")) {
            System.out.println("The | character is not allowed in fields.");
            return;
        }
        if (gender.length() != 1 || "MFO".indexOf(gender.charAt(0)) < 0) {
            System.out.println("Gender must be M, F, or O.");
            return;
        }
        for (User existing : users) {
            if (existing.getUsername().equalsIgnoreCase(username) ||
                    existing.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Username or email already exists.");
                return;
            }
        }

        User user = new User(nextId, username, password, gender.charAt(0), email, address);
        users.add(user);
        if (saveUsers()) {
            nextId++;
            System.out.println("Registration successful. Your user ID is " + user.getId());
        } else {
            users.remove(user);
            System.out.println("Registration failed because the file could not be saved.");
        }
    }

    public User loginUser(Scanner sc) {
        System.out.println("\n===== User Login =====");
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine();

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                System.out.println("Login successful. Welcome, " + user.getUsername() + "!");
                return user;
            }
        }
        System.out.println("Invalid username or password.");
        return null;
    }

    public void viewUsers() {
        if (users.isEmpty()) {
            System.out.println("No registered users.");
            return;
        }
        System.out.println("\n===== Registered Users =====");
        for (User user : users) {
            System.out.println("ID: " + user.getId() + ", Username: " + user.getUsername()
                    + ", Email: " + user.getEmail());
        }
    }

    private boolean saveUsers() {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = new ArrayList<>();
            for (User user : users) {
                lines.add(user.getId() + "|" + user.getUsername() + "|" + user.getPassword()
                        + "|" + user.getGender() + "|" + user.getEmail() + "|" + user.getAddress());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
            return false;
        }
    }

    private void loadUsers() {
        try {
            Files.createDirectories(filePath.getParent());
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                return;
            }
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                String[] data = line.split("\\|", -1);
                if (data.length != 6) continue;
                try {
                    int id = Integer.parseInt(data[0]);
                    if (data[3].length() != 1) continue;
                    users.add(new User(id, data[1], data[2], data[3].charAt(0), data[4], data[5]));
                    nextId = Math.max(nextId, id + 1);
                } catch (NumberFormatException ignored) {
                    System.out.println("Skipping invalid user record.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }
}
