package grabit;

import java.sql.Connection;
import java.sql.PreparedStatement;
abstract class PaymentHandler {

    protected double totalAmount;
    protected String currentUser;
    protected int currentUserId;
    protected Connection con;

    public PaymentHandler(double totalAmount, String currentUser, int currentUserId, Connection con) {
        this.totalAmount = totalAmount;
        this.currentUser = currentUser;
        this.currentUserId = currentUserId;
        this.con = con;
    }


    public abstract String executePayment();


    public boolean processOrder() {
        try {
            String paymentMethod = executePayment();

            if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                System.out.println("Payment successful via " + paymentMethod + "!");

                // Insert payment record
                String payQuery = "INSERT INTO payments (user_name, amount, payment_method, payment_date) VALUES (?, ?, ?, NOW())";
                try (PreparedStatement pst = con.prepareStatement(payQuery)) {
                    pst.setString(1, currentUser);
                    pst.setDouble(2, totalAmount);
                    pst.setString(3, paymentMethod);
                    pst.executeUpdate();
                }


                String orderQuery = "INSERT INTO orders (user_name, product_name, quantity, total_amount, order_date) VALUES (?, ?, ?, ?, NOW())";
                try (PreparedStatement pst = con.prepareStatement(orderQuery)) {
                    for (Login.CartItem item : Login.cart) {
                        pst.setString(1, currentUser);
                        pst.setString(2, item.name);
                        pst.setInt(3, item.quantity);
                        pst.setDouble(4, item.price * item.quantity);
                        pst.executeUpdate();
                    }
                }

                String clearCartQuery = "DELETE FROM addcart WHERE user_id = ?";
                try (PreparedStatement pst = con.prepareStatement(clearCartQuery)) {
                    pst.setInt(1, currentUserId);
                    pst.executeUpdate();
                }

                Login.cart.clear();
                System.out.println("Order placed successfully. Thank you for shopping with GRAB-IT!");
                return true;
            } else {
                System.out.println("Payment was cancelled.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
