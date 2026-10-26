# E-Commerce Shopping Cart System (Core Java)

A menu-driven console project demonstrating OOP, Collections Framework, exception handling, and text-file persistence.

## Requirements
- JDK 17 or later (the menu uses modern switch expressions)
- Any Java IDE or terminal

## Project structure
```text
EcommerceShoppingCart/
├── src/
│   └── com/
│       ├── menu/Main.java
│       ├── user/User.java
│       ├── user/UserService.java
│       ├── product/Product.java
│       ├── product/ProductService.java
│       └── cart/CartItem.java
│       └── cart/ShoppingCart.java
└── data/                 # created automatically when the program runs
    ├── users.txt
    └── products.txt
```

## Run from a terminal
Open a terminal in the project folder and run:

```bash
javac -d out src/com/user/User.java src/com/user/UserService.java src/com/product/Product.java src/com/product/ProductService.java src/com/cart/CartItem.java src/com/cart/ShoppingCart.java src/com/menu/Main.java
java -cp out com.menu.Main
```

The `data` directory is created automatically. Product sample data is added on the first run.

## Menu
1. Register user
2. Login
3. Browse products
4. Add product to cart (login required)
5. View cart (login required)
6. Checkout (login required)
7. Admin operations
8. Logout
9. Exit

For this educational demo, the admin password is `admin123`. Change it before sharing the project.

## Important security note
This is a learning project. User passwords are stored as plain text in `data/users.txt`, and the admin password is hard-coded. Do not use this implementation for real customers or production data. A production system needs secure password hashing, proper authorization, validation, and transaction-safe storage.
