Grabit: A Java-Based Console E-commerce Management System

Grabit is a full-featured, console-based application built in Java that simulates a basic e-commerce platform. It provides separate interfaces for both administrators and users, managing core e-commerce functionalities through a command-line interface. The system uses a MySQL database to handle persistent storage for product, user, order, and payment data.

Key Features:

User & Admin Modules: The application provides two distinct entry points. The User module handles shopping, including product browsing, cart management, and payment processing. The Admin module offers complete control over the store, including product, category, and delivery partner management.

Data Structures: Efficient in-memory data structures are used to optimize performance. A Binary Search Tree (BST) manages product categories, allowing for fast searches and organized display. A Singly Linked List temporarily stores products added by the admin for quick viewing within the current session.

Database Integration: All critical data is stored in a MySQL database, ensuring data persistence and integrity. The application uses JDBC for seamless database connectivity.

Payment System: The system supports multiple payment methods, including UPI and Cash on Delivery (COD), implemented using the Strategy Design Pattern for flexible and extensible payment handling.

Operations & Reporting: The admin dashboard provides valuable insights through features like sales reports, low-stock alerts, and a view of recent orders.

Billing: After a successful order, the application generates a detailed bill in a .txt file for the customer, providing a clear record of the transaction.

This project serves as a comprehensive example of building a data-driven application in Java, demonstrating fundamental concepts such as object-oriented programming (OOP), database management, and common data structures and design patterns.
