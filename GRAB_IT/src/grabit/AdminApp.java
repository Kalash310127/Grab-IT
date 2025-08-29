package grabit;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class AdminApp {
    static final String dburl = "jdbc:mysql://localhost:3306/searchproduct";
    static final String dbuser = "root";
    static final String dbpass = "";
    static final String driverName = "com.mysql.cj.jdbc.Driver";
    static final SinglyLinkedList pro = new SinglyLinkedList();
    static final CategoryBST categoryTree = new CategoryBST();
    static final Scanner sc = new Scanner(System.in);


    public void App() throws Exception {
        Class.forName(driverName);

        int choice;
        do {
            System.out.println("--- ADMIN DASHBOARD ---");
            System.out.println("1. Product Management");
            System.out.println("2. Category Management");
            System.out.println("3. Customer Management");
            System.out.println("4. Delivery Partner Management");
            System.out.println("5. Store Operations");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    productManagement();
                    break;
                case 2:
                    categoryManagement();
                    break;
                case 3:
                    customerManagement();
                    break;
                case 4:
                    deliveryPartnerManagement();
                    break;
                case 5:
                    storeOperations();
                    break;
                case 6:
                    System.out.println("Exiting Admin Console...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 6);
    }

    public static void productManagement() throws Exception {
        Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
        int choice;
        do {
            System.out.println("--- Product Management ---");
            System.out.println("1. View Products");
            System.out.println("2. Add Product");
            System.out.println("3. Update Product Price");
            System.out.println("4. Delete Product");
            System.out.println("5. View Product Which you have Added");
            System.out.println("6. Back");
            System.out.println("Enter you choice:");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    String sql = "select * from product";
                    PreparedStatement pst = con.prepareStatement(sql);
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        System.out.println(rs.getInt("p_id") + " | " + rs.getString("p_name") +
                                " | ₹" + rs.getDouble("p_price") + " | Stock: " + rs.getInt("p_stock"));
                    }
                    break;
                case 2:
                    System.out.print("Enter product name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter price: ");
                    double price = sc.nextDouble();
                    System.out.print("Enter stock: ");
                    int stock = sc.nextInt();
                    System.out.print("Enter category ID: ");
                    int cat = sc.nextInt();
                    sc.nextLine();


                    Product p = new Product(name, price, stock, cat);
                    pro.addLast(p);
                    System.out.println("Product added to Linked list.");

                    String sql1 = "insert into product (p_name, p_price, p_stock, p_catid) Values (?, ?, ?, ?)";
                    PreparedStatement pst1 = con.prepareStatement(sql1);
                    pst1.setString(1, name);
                    pst1.setDouble(2, price);
                    pst1.setInt(3, stock);
                    pst1.setInt(4, cat);
                    pst1.executeUpdate();
                    System.out.println("Product added to DB.");
                    break;
                case 3:
                    System.out.print("Enter product ID: ");
                    int id = sc.nextInt();
                    System.out.print("Enter new price: ");
                    double newPrice = sc.nextDouble();

                    String sql2= "update product set p_price=? where p_id=?";
                    PreparedStatement pst2 = con.prepareStatement(sql2);
                    pst2.setDouble(1, newPrice);
                    pst2.setInt(2, id);
                    pst2.executeUpdate();
                    System.out.println("Price updated");
                    break;
                case 4:
                    System.out.print("Enter product ID to delete: ");
                    int id1 = sc.nextInt();
                    String sql3 = "delete from product where p_id = ?";
                    PreparedStatement pst3 = con.prepareStatement(sql3);
                    pst3.setInt(1, id1);
                    pst3.executeUpdate();
                    System.out.println("Product deleted");
                    break;
                case 5:
                    pro.display();
                    break;
                case 6:
                    System.out.println("Exiting Product Managment");
                    break;
            }
        } while (choice != 6);

    }

    public static void categoryManagement() throws Exception {
        Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
        try {
            String sql = "select * from categories";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Categorie c = new Categorie(rs.getInt("c_id"), rs.getString("c_name"));
                categoryTree.insert(c);
            }
        } catch (SQLException e) {
            System.err.println("Error loading categories into BST: " + e.getMessage());
        }
        int choice;
        do {
            System.out.println("--- Category Management ---");
            System.out.println("1. View Categories");
            System.out.println("2. Add Category");
            System.out.println("3. Delete Category");
            System.out.println("4. Back");
            System.out.println("Enter you choice :");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    categoryTree.displayCategories();
                    break;
                case 2:
                    System.out.print("Enter category name: ");
                    String name = sc.nextLine();
                    String sql1 = "INSERT INTO categories(c_name) values(?)";
                    try (PreparedStatement pst1 = con.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS)) {
                        pst1.setString(1, name);
                        int affectedRows = pst1.executeUpdate();
                        if (affectedRows > 0) {
                            try (ResultSet generatedKeys = pst1.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    int newCatId = generatedKeys.getInt(1);
                                    Categorie newCategory = new Categorie(newCatId, name);
                                    categoryTree.insert(newCategory);
                                    System.out.println("Category added to DB and BST.");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.err.println("Error adding category: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Enter category ID to delete: ");
                    int id = sc.nextInt();

                    String sql2 = "DELETE FROM categories WHERE c_id = ?";
                    try (PreparedStatement pst2 = con.prepareStatement(sql2)) {
                        pst2.setInt(1, id);
                        int affectedRows = pst2.executeUpdate();
                        if (affectedRows > 0) {
                            categoryTree.delete(id);
                            System.out.println("Category deleted from DB and BST.");
                        } else {
                            System.out.println("Category not found in DB.");
                        }
                    } catch (SQLException e) {
                        System.err.println("Error deleting category from DB: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Exiting Category Management");
                    break;
            }
        } while (choice != 4);
    }

    public static void customerManagement() throws Exception {
        Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
        System.out.println("--- Customer List ---");
        String sql = "SELECT user_name FROM orders";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            System.out.println("Customer: " + rs.getString("user_name"));
        }
    }

    public static void deliveryPartnerManagement() throws Exception {
        Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
        int choice;
        do {
            System.out.println("--- Delivery Partner Management ---");
            System.out.println("1. View Partners");
            System.out.println("2. Add Partner");
            System.out.println("3. Remove Partner");
            System.out.println("4. Back");
            System.out.println("Enter your choice");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    String sql = "SELECT * FROM delivery_partners";
                    PreparedStatement pst = con.prepareStatement(sql);
                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + ": " + rs.getString("name") +
                                " | Phone: " + rs.getString("phone"));
                    }
                    break;
                case 2:
                    System.out.print("Enter partner name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter phone: ");
                    String phone = sc.nextLine();
                    String sql1 = "INSERT INTO delivery_partners (name, phone) VALUES (?, ?)";
                    PreparedStatement pst1 = con.prepareStatement(sql1);
                    pst1.setString(1, name);
                    pst1.setString(2, phone);
                    pst1.executeUpdate();
                    System.out.println("Partner added.");
                    break;
                case 3:
                    System.out.print("Enter partner ID to remove: ");
                    int id = sc.nextInt();
                    String sql2="DELETE FROM delivery_partners WHERE id = ?";
                    PreparedStatement delPst = con.prepareStatement(sql2);
                    delPst.setInt(1, id);
                    delPst.executeUpdate();
                    System.out.println("Partner removed.");
                    break;
            }
        } while (choice != 4);

    }

    public static void storeOperations() {
        int choice;

        do {
            System.out.println("--- Store Operation ---");
            System.out.println("1. View Recent orders");
            System.out.println("2. View Total sales");
            System.out.println("3. View Top Selling products");
            System.out.println("4. Check Low Stock products");
            System.out.println("5. Generate Sales Report");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    String sql = "SELECT * FROM orders WHERE order_date = CURDATE()";
                    try (Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
                         PreparedStatement pst = con.prepareStatement(sql);
                         ResultSet rs = pst.executeQuery()) {

                        while (rs.next()) {
                            System.out.println();
                            System.out.println("Order ID = " + rs.getInt(1));
                            System.out.println("User Name = " + rs.getString(2));
                            System.out.println("Product Name = " + rs.getString(3));
                            System.out.println("Quantity = " + rs.getInt(4));
                            System.out.println("Total amount = " + rs.getDouble(5));
                            System.out.println("Date = " + rs.getDate(6));
                            System.out.println("-------------------------------");
                        }

                    } catch (Exception e) {
                        System.err.println("Error fetching today's orders: " + e.getMessage());
                    }
                    break;

                case 2:
                    String sql1 = "select user_name,product_name,total_amount from orders ";
                    try (Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
                         PreparedStatement pst = con.prepareStatement(sql1);
                         ResultSet rs = pst.executeQuery()) {
                        System.out.printf("%-20s | %-15s | %-10s%n", "User Name", "Product name","Amount");
                        System.out.println("-----------------------------");
                        while (rs.next()) {
                            String Uname = rs.getString(1);
                            String Pname = rs.getString(2);
                            Double amt = rs.getDouble(3);
                            System.out.printf("%-20s | %-15s | %-10.2f%n", Uname, Pname, amt);
                        }

                    } catch (Exception e) {
                        System.out.println("Error in fetching total sales " + e.getMessage());
                    }
                    break;
                case 3:
                    String sql2 = "SELECT product_name, quantity FROM orders";
                    Map<String, Integer> salesMap = new HashMap<>();

                    try (Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
                         PreparedStatement pst = con.prepareStatement(sql2);
                         ResultSet rs = pst.executeQuery()) {

                        while (rs.next()) {
                            String product = rs.getString("product_name");
                            int qty = rs.getInt("quantity");
                            salesMap.merge(product, qty, Integer::sum);

                        }

                        System.out.printf("\n--- Top Selling Products ---%n");
                        System.out.printf("%-20s | %-10s%n", "Product Name", "Qty Sold");
                        System.out.println("-----------------------------");

                        salesMap.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEach(entry ->
                                System.out.printf("%-20s | %-10d%n", entry.getKey(), entry.getValue()));

                    } catch (SQLException e) {
                        System.err.println("Error retrieving top selling products: " + e.getMessage());
                    }
                    break;
                case 4:
                    String sql3 = "SELECT p_id, p_name, p_stock FROM product WHERE p_stock <= 10";
                    try (Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
                         PreparedStatement pst = con.prepareStatement(sql3);
                         ResultSet rs = pst.executeQuery()) {

                        System.out.printf("%-10s | %-30s | %-10s%n", "Product ID", "Product Name", "Stock");
                        System.out.println("---------------------------------------------------------------");

                        while (rs.next()) {
                            int id = rs.getInt(1);
                            String name = rs.getString(2);
                            int stock = rs.getInt(3);
                            System.out.printf("%-10d | %-30s | %-10d%n", id, name, stock);
                        }

                    } catch (Exception e) {
                        System.out.println("Error fetching low stock products: " + e.getMessage());
                    }

                    break;
                case 5:
                    printSalesReport();
                    break;
                case 6:
                    System.out.println("Exiting Store Operations...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);
    }

    public static void printSalesReport() {
        String sql = "SELECT order_id, user_name, product_name, quantity, total_amount, order_date FROM orders";

        try (Connection con = DriverManager.getConnection(dburl, dbuser, dbpass);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            System.out.println("===== SALES REPORT =====");
            System.out.printf("%-8s | %-15s | %-20s | %-4s | %-10s | %-10s%n",
                    "OrderID", "User", "Product", "Qty", "Total", "Date");
            System.out.println("-------------------------------------------------------------------------------");

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String user = rs.getString("user_name");
                String product = rs.getString("product_name");
                int qty = rs.getInt("quantity");
                double total = rs.getDouble("total_amount");
                Date date = rs.getDate("order_date");

                System.out.printf("%-8d | %-15s | %-20s | %-4d | %-10.2f | %-10s%n",
                        orderId, user, product, qty, total, date.toString());
            }

            System.out.println("===== END OF REPORT =====");

        } catch (SQLException e) {
            System.err.println("Error generating console sales report: " + e.getMessage());
        }
    }
}