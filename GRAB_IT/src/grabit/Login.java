package grabit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Login {
    static String currentuser;
    static ArrayList<CartItem> cart = new ArrayList<>();
    static String mobileNo;
    static String address;
    static int currentuserid;

    static class CartItem {
        int id;
        String name;
        double price;
        int quantity;

        CartItem(int id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }

    public void App() throws Exception {
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        Connection con = DriverManager.getConnection(dburl, user, pass);
        Scanner sc = new Scanner(System.in);

        System.out.println("Hello user!!!!!\n");
        System.out.println("Welcome to GRAB-IT");

        boolean mobileValid = false;
        boolean otpVerified = false;
        boolean userExists = false;
        String email = null;
        String address = null;

        // Step 1: Mobile number input and validation
        String mobileNo = null;
        do {
            System.out.println("Enter your mobile number: ");
            mobileNo = sc.nextLine().trim();

            if (mobileNo.length() == 10 && mobileNo.startsWith("9")) {
                System.out.println("✅ Valid phone number");
                mobileValid = true;

                // Check if mobile exists
                String checkQuery = "SELECT id, name, email FROM users WHERE mobile = ?";
                PreparedStatement checkPst = con.prepareStatement(checkQuery);
                checkPst.setString(1, mobileNo);
                ResultSet rs = checkPst.executeQuery();

                if (rs.next()) {
                    currentuserid = rs.getInt("id");
                    currentuser = rs.getString("name");
                    System.out.println("📲 Mobile number already registered.");
                    System.out.println("Welcome back, " + currentuser + "! Your user ID is " + currentuserid);
                    userExists = true;
                }
            } else {
                System.out.println("❌ Invalid number. Must be 10 digits and start with '9'.");
            }
        } while (!mobileValid);

        // Step 2: If user is new, proceed with registration
        if (!userExists) {
            System.out.println("Welcome new user. Please register.");

            // Name input
            System.out.println("Enter your name:");
            currentuser = sc.nextLine().trim();

            // OTP verification
            do {
                Random r1 = new Random();
                int otpGenerated = 1000 + r1.nextInt(8888);
                System.out.println("Your OTP is: " + otpGenerated);
                System.out.println("Enter the OTP:");
                int otpInput = sc.nextInt();
                sc.nextLine(); // consume newline

                if (otpInput == otpGenerated) {
                    System.out.println("✅ OTP verified");
                    otpVerified = true;
                } else {
                    System.out.println("❌ Incorrect OTP. Try again.");
                }
            } while (!otpVerified);

            // Email input
            do {
                System.out.println("Enter your email:");
                email = sc.next().trim();

                if (email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
                    System.out.println("✅ Email accepted");
                    break;
                } else {
                    System.out.println("❌ Invalid email. Only @gmail.com addresses are allowed.");
                }
            } while (true);

            sc.nextLine(); // consume leftover newline

            // Address input
            System.out.println("Enter your address:");
            address = sc.nextLine();

            // Pin code input
            do {
                System.out.println("Enter pin code:");
                long pin = sc.nextLong();
                if (pin >= 100000 && pin <= 999999) {
                    System.out.println("✅ Valid pin");
                    break;
                } else {
                    System.out.println("❌ Invalid pin. Must be 6 digits.");
                }
            } while (true);

            // Insert new user
            String insertQuery = "INSERT INTO users (name, mobile, email) VALUES (?, ?, ?)";
            PreparedStatement insertPst = con.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            insertPst.setString(1, currentuser);
            insertPst.setString(2, mobileNo);
            insertPst.setString(3, email);
            insertPst.executeUpdate();

            ResultSet generatedKeys = insertPst.getGeneratedKeys();
            if (generatedKeys.next()) {
                currentuserid = generatedKeys.getInt(1);
                System.out.println("🎉 Registration successful! Your user ID is: " + currentuserid);
            }
        }

        // Step 3: Launch main menu
        System.out.println("Hello " + currentuser + "\nWelcome to GRAB-IT");

        int choice;
        do {
            System.out.println("\nMain menu");
            System.out.println("1.Search products\n2.Browse categories\n3.View cart\n4.Payment\n5.Settings\n6.Exit");
            System.out.println("Enter your choice");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("Search Products");
                    try {
                        searchproduct();
                    } catch (Exception e) {
                        System.out.println("Error in searching products: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.println("Browse Categories");
                    try {
                        BrowseCategories();
                    } catch (Exception e) {
                        System.out.println("Error in browsing categories: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("View cart");
                    Viewcart();
                    break;
                case 4:
                    System.out.println("Payment");
                    try {
                        makePayment();
                    } catch (Exception e) {
                        System.out.println("Payment failed: " + e.getMessage());
                    }
                    break;
                case 5:
                    System.out.println("Settings");
                    try {
                        showSettings();
                    } catch (Exception e) {
                        System.out.println("Error in settings: " + e.getMessage());
                    }
                    break;
                case 6:
                    System.out.println("Exit");
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        } while (choice != 6);

        sc.close();
    }

    public static void searchproduct() throws Exception {
        Scanner sc = new Scanner(System.in);
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);

        try (Connection con = DriverManager.getConnection(dburl, user, pass)) {
            String query = "SELECT product.p_id, product.p_name, product.p_price, product.p_stock, categories.c_name " +
                    "FROM product JOIN categories ON product.p_catid = categories.c_id " +
                    "WHERE product.p_name LIKE ?";
            PreparedStatement pst = con.prepareStatement(query);
            System.out.println("Enter the product you want to search");
            String product = sc.nextLine();
            pst.setString(1, product);
            ResultSet rs = pst.executeQuery();
            boolean found = false;
            System.out.println("Search Results:");
            System.out.println("--------------------------------------------------");
            while (rs.next()) {
                found = true;
                System.out.println("ID       : " + rs.getInt("p_id"));
                System.out.println("Name     : " + rs.getString("p_name"));
                System.out.println("Price    : ₹" + rs.getDouble("p_price"));
                System.out.println("Stock    : " + rs.getInt("p_stock"));
                System.out.println("Category : " + rs.getString("c_name"));
                System.out.println("--------------------------------------------------");
            }
            if (!found) {
                System.out.println("No products found for your search.");
            }
            int n;
            do {
                System.out.println("1.Add to cart\n2.Buy now\n3.Exit");
                System.out.println("Enter your choice");
                n = sc.nextInt();
                sc.nextLine();
                switch (n) {
                    case 1:
                        Addtocart();
                        break;
                    case 2:
                        makePayment();
                        break;
                    case 3:
                        System.out.println("Exit\nThank you for visiting!!!");
                        break;
                    default:
                        System.out.println("Please enter valid choice");
                        break;
                }
            } while (n != 3);
        }
    }

    public static void BrowseCategories() throws Exception {
        Scanner sc = new Scanner(System.in);
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);

        try (Connection con = DriverManager.getConnection(dburl, user, pass)) {
            String query = "SELECT * FROM categories";
            try (PreparedStatement pst = con.prepareStatement(query);
                 ResultSet Rs = pst.executeQuery()) {
                System.out.println("Categories:");
                while (Rs.next()) {
                    System.out.println(Rs.getInt("c_id") + ". " + Rs.getString("c_name").trim());
                }
            }

            System.out.println("\nEnter category ID to view products: ");
            int catId = sc.nextInt();
            sc.nextLine();

            String prodQuery = "SELECT p_id, p_name, p_price, p_stock FROM product WHERE p_catid = ?";
            PreparedStatement prodSt = con.prepareStatement(prodQuery);
            prodSt.setInt(1, catId);
            ResultSet prodRs = prodSt.executeQuery();
            System.out.println("\nProducts in Selected Category:");
            System.out.println("--------------------------------------------------------");
            boolean found = false;
            while (prodRs.next())
            {
                found = true;
                System.out.println("Product ID   : " + prodRs.getInt("p_id"));
                System.out.println("Name         : " + prodRs.getString("p_name"));
                System.out.println("Price        : ₹" + prodRs.getDouble("p_price"));
                System.out.println("Stock        : " + prodRs.getInt("p_stock"));
                System.out.println("--------------------------------------------");
            }
            if (!found) {
                System.out.println("No products found in this category.");
            }
            searchproduct();
            int n;
            do {
                System.out.println("1.Add to cart\n2.Buy now\n3.Exit");
                System.out.println("Enter your choice");
                n = sc.nextInt();
                sc.nextLine();
                switch (n) {
                    case 1:
                        Addtocart();
                        break;
                    case 2:
                        makePayment();
                        break;
                    case 3:
                        System.out.println("Exit\nThank you for visiting!!!");
                        break;
                    default:
                        System.out.println("Please enter valid choice");
                        break;
                }
            } while (n != 3);
        }
    }

    public static void Addtocart() throws Exception {
        Scanner sc = new Scanner(System.in);
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);

        try (Connection con = DriverManager.getConnection(dburl, user, pass)) {
            System.out.println("Enter product ID to add to cart:");
            int pid = sc.nextInt();

            System.out.println("Enter quantity:");
            int quantity = sc.nextInt();

            String selectQuery = "SELECT p_name, p_price, p_stock FROM product WHERE p_id = ?";
            try (PreparedStatement pst = con.prepareStatement(selectQuery)) {
                pst.setInt(1, pid);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        int stock = rs.getInt("p_stock");
                        if (quantity > stock) {
                            System.out.println("Not enough stock available. Only " + stock + " items in stock.");
                            return;
                        }

                        String name = rs.getString("p_name");
                        double price = rs.getDouble("p_price");

                        CartItem item = new CartItem(pid, name, price, quantity);
                        cart.add(item);

                        String insertQuery = "INSERT INTO addcart (product_id, quantity, user_id) VALUES (?, ?, ?)";
                        try (PreparedStatement insertPst = con.prepareStatement(insertQuery)) {
                            insertPst.setInt(1, pid);
                            insertPst.setInt(2, quantity);
                            insertPst.setInt(3, currentuserid);
                            insertPst.executeUpdate();
                            System.out.println("Product added to cart successfully.");
                        }
                    } else {
                        System.out.println("Product not found with ID: " + pid);
                    }
                }
            }
        }
    }
    public static void Viewcart() throws Exception {
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);

        try (Connection con = DriverManager.getConnection(dburl, user, pass)) {
            System.out.println("--- Your Cart ---");

            // First show items from database
            String sql = "SELECT a.cart_id, p.p_id, p.p_name, p.p_price, a.quantity " +
                    "FROM addcart a " +
                    "JOIN product p ON a.product_id = p.p_id " +
                    "WHERE a.user_id = ?";

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, currentuserid);
                ResultSet rs = pst.executeQuery();

                double total = 0;
                boolean empty = true;

                while (rs.next()) {
                    empty = false;
                    System.out.println("Cart ID   : " + rs.getInt("cart_id"));
                    System.out.println("ProductID : " + rs.getInt("p_id"));
                    System.out.println("Name      : " + rs.getString("p_name"));
                    System.out.println("Price     : ₹" + rs.getDouble("p_price"));
                    System.out.println("Quantity  : " + rs.getInt("quantity"));
                    System.out.println("-------------------------------------");

                    total += rs.getDouble("p_price") * rs.getInt("quantity");
                }

                if (empty) {
                    System.out.println("Your cart is empty.");
                } else {
                    System.out.println("Total Amount: ₹" + total);
                }
            }
        }
    }



    public static void showSettings() throws Exception {
        Scanner sc = new Scanner(System.in);
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        int option;
        do {
            System.out.println("\nSettings Menu:");
            System.out.println("1. View Order History");
            System.out.println("2. View Payment History");
            System.out.println("3. New Address");
            System.out.println("4. View AddressBook");
            System.out.println("5. Profile");
            System.out.println("6. Track orders");
            System.out.println("7. Logout to Main Menu");

            System.out.print("Enter your choice: ");
            option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    viewOrderHistory();
                    break;
                case 2:
                    viewPaymentHistory();
                    break;
                case 3:
                    newAddress();
                    break;
                case 4:
                    viewAddressBook();
                    break;
                case 5:
                    viewProfile();
                    break;
                case 6:
                    trackOrders();
                    break;
                case 7:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        } while (option != 7);
    }

    public static void trackOrders() throws Exception {
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection con = DriverManager.getConnection(dburl, user, pass)) {
            String query = "SELECT order_id, product_name, order_date FROM orders WHERE user_name = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, currentuser);
            ResultSet rs = pst.executeQuery();

            System.out.println("\n--- Order Tracking ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Order ID   : " + rs.getInt("order_id"));
                System.out.println("Product    : " + rs.getString("product_name"));
                System.out.println("Date       : " + rs.getDate("order_date"));
                System.out.println("-----------------------------");
            }
            if (!found) {
                System.out.println("No orders found.");
            }
        }
    }

    public static void viewOrderHistory() throws Exception {
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        System.out.println("\n--- Order History ---");
        Connection con = DriverManager.getConnection(dburl, user, pass);
        String query = "SELECT * FROM orders WHERE user_name = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, currentuser);
        ResultSet rs = pst.executeQuery();

        boolean found = false;
        while (rs.next())
        {
            found = true;
            System.out.println("Order ID  : " + rs.getInt("order_id"));
            System.out.println("Product   : " + rs.getString("product_name"));
            System.out.println("Quantity  : " + rs.getInt("quantity"));
            System.out.println("Total     : ₹" + rs.getDouble("total_amount"));
            System.out.println("Date      : " + rs.getDate("order_date"));
            System.out.println("-----------------------------");
        }
        if (!found) {
            System.out.println("No order history found.");
        }
    }

    public static void viewPaymentHistory() throws Exception {
        System.out.println("\n--- Payment History ---");
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        System.out.println("\n--- Order History ---");
        Connection con = DriverManager.getConnection(dburl, user, pass);
        String query = "SELECT * FROM payments WHERE user_name = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, currentuser);
        ResultSet rs = pst.executeQuery();

        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("Payment ID : " + rs.getInt("payment_id"));
            System.out.println("Amount     : ₹" + rs.getDouble("amount"));
            System.out.println("Method     : " + rs.getString("payment_method"));
            System.out.println("Date       : " + rs.getDate("payment_date"));
            System.out.println("-----------------------------");
        }

        if (!found) {
            System.out.println("No payment history found.");
        }
    }
    public static void viewProfile() throws Exception {
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection con = DriverManager.getConnection(dburl, user, pass)) {
            String query = "SELECT * FROM users WHERE name = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, currentuser);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- User Profile ---");
                System.out.println("Name   : " + rs.getString("name"));
                System.out.println("Mobile : " + rs.getString("mobile"));
                System.out.println("Email  : " + rs.getString("email"));
            } else {
                System.out.println("Profile not found.");
            }
        }
    }
    public static void newAddress() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Add New Address ---");

        try {
            System.out.print("Enter address type (Home/Work): ");
            String type = sc.nextLine();

            System.out.print("Enter full address: ");
            String full = sc.nextLine();

            System.out.print("Enter phone number: ");
            String phone = sc.nextLine();

            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/searchproduct", "root", "")) {

                String sql = "INSERT INTO addresses (user_name, address_type, full_address, phone_number) " +
                        "VALUES (?, ?, ?, ?)";

                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, currentuser);
                    pst.setString(2, type);
                    pst.setString(3, full);
                    pst.setString(4, phone);

                    int rows = pst.executeUpdate();
                    if (rows > 0) {
                        System.out.println("✓ Address added successfully!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewAddressBook() throws Exception {
        System.out.println("\n--- Address Book ---");
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection con = DriverManager.getConnection(dburl, user, pass)) {
            String query = "SELECT id, address_type, full_address, phone_number " +
                    "FROM addresses WHERE user_name = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, currentuser);
            ResultSet rs = pst.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Type: " + rs.getString("address_type"));
                System.out.println("Address: " + rs.getString("full_address"));
                System.out.println("Phone: " + rs.getString("phone_number"));
                System.out.println("-----------------------------");
            }

            if (!found) {
                System.out.println("No addresses found for this user.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing addresses: " + e.getMessage());
        }
    }


    public static void makePayment() throws Exception {
        String dburl = "jdbc:mysql://localhost:3306/searchproduct";
        String user = "root";
        String pass = "";

        Connection con = null;
        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(dburl, user, pass);

            if (cart.isEmpty()) {
                System.out.println("Cart was empty. Please add to cart first");
                return;
            }

            double total = 0;
            for (CartItem item : cart) {
                total += item.price * item.quantity;
            }

            System.out.println("\n--- Payment Summary ---");
            System.out.println("Total Amount: ₹" + total);
            System.out.println("Choose payment method:");
            System.out.println("1. UPI");
            System.out.println("2. Cash on Delivery (COD)");

            int choice = sc.nextInt();
            sc.nextLine();

            PaymentHandler paymentHandler = null;

            if (choice == 1) {
                paymentHandler = new UpiPaymentHandler(total, currentuser, currentuserid, con);
            } else if (choice == 2) {
                paymentHandler = new CodPaymentHandler(total, currentuser, currentuserid, con);
            } else {
                System.out.println("Invalid payment option. Payment cancelled.");
                return;
            }

            boolean orderSuccessful = paymentHandler.processOrder();



            if (orderSuccessful) {
                System.out.println("Generating bill...");
                // Retrieve the latest order ID
                int latestOrderId = 0;
                String sql = "SELECT order_id FROM orders WHERE user_name = ? ORDER BY order_date DESC LIMIT 1";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, currentuser);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            latestOrderId = rs.getInt("order_id");
                        }
                    }
                }

                String fileName = "Bill_" + latestOrderId + "_" + currentuser.replaceAll("\\s+", "") + ".txt";
                try (FileWriter fw = new FileWriter(fileName);
                     BufferedWriter bw = new BufferedWriter(fw)) {

                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = now.format(formatter);

                    bw.write("========== GRAB-IT ORDER BILL ==========");
                    bw.newLine();
                    bw.write("Order ID: " + latestOrderId);
                    bw.newLine();
                    bw.write("Customer: " + currentuser);
                    bw.newLine();
                    bw.write("Mobile No: " + mobileNo);
                    bw.newLine();
                    bw.write("Delivery Address: " + address);
                    bw.newLine();
                    bw.write("Order Date: " + formattedDate);
                    bw.newLine();
                    bw.write("----------------------------------------");
                    bw.newLine();


                    bw.write(String.format("%-5s %-25s %-10s %-10s %-10s", "ID", "Product Name", "Price", "Qty", "Total"));
                    bw.newLine();
                    bw.write("---------------------------------------------------------------------------");
                    bw.newLine();

                    String orderItemsSql = "SELECT o.product_name, o.quantity, o.total_amount, p.p_id " +
                            "FROM orders o " +
                            "JOIN product p ON o.product_name = p.p_name " +
                            "WHERE o.order_id = ?";
                    try (PreparedStatement itemPst = con.prepareStatement(orderItemsSql)) {
                        itemPst.setInt(1, latestOrderId);
                        try (ResultSet itemRs = itemPst.executeQuery()) {
                            while (itemRs.next()) {
                                int p_id = itemRs.getInt("p_id"); // Now you can get the ID
                                String p_name = itemRs.getString("product_name");
                                int quantity = itemRs.getInt("quantity");
                                double itemTotal = itemRs.getDouble("total_amount");
                                double p_price = itemTotal / quantity;

                                bw.write(String.format("%-5d %-25s %-10.2f %-10d %-10.2f",
                                        p_id, p_name, p_price, quantity, itemTotal));
                                bw.newLine();
                            }
                        }
                    }

                    bw.write("---------------------------------------------------------------------------");
                    bw.newLine();
                    bw.write(String.format("%-50s ₹%.2f", "Total Amount:", total));
                    bw.newLine();
                    bw.write("Payment Method: " + (choice == 1 ? "UPI" : "Cash on Delivery"));
                    bw.newLine();
                    bw.write("---------------------------------------------------------------------------");
                    bw.newLine();
                    bw.write("Thank you for shopping with GRAB-IT!");
                    bw.newLine();

                    System.out.println("Payment successful! Your bill has been generated: " + fileName);
                }
            }
            else {
                System.out.println("Payment failed. No bill was generated.");
            }
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
}