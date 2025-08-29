package grabit;

import java.sql.Connection;

class CodPaymentHandler extends PaymentHandler {boolean cod=false;

    public CodPaymentHandler(double totalAmount, String currentUser, int currentUserId, Connection con) {
        super(totalAmount, currentUser, currentUserId, con);
    }

    @Override
    public String executePayment() {
        System.out.println("You chose Cash on Delivery. Please keep ₹" + totalAmount + " ready at delivery.");
        return "Cash on Delivery"; // Return method name
    }
}