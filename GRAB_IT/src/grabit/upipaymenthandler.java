package grabit;

import java.sql.Connection;
import java.util.Scanner;

class UpiPaymentHandler extends PaymentHandler {

    public UpiPaymentHandler(double totalAmount, String currentUser, int currentUserId, Connection con) {
        super(totalAmount, currentUser, currentUserId, con);
    }

    @Override
    public String executePayment() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter UPI ID (e.g., name@upi): ");
        String upiId = sc.nextLine();
        if (upiId.contains("@")) {
            System.out.println("Processing UPI payment for ₹" + totalAmount + "...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "UPI";
        } else {
            System.out.println("Invalid UPI ID format.");
            return null;
        }
    }
}