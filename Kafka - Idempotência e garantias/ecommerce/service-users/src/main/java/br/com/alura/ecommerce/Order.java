package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String orderID;
    private final String email;
    private final BigDecimal amount;

    public Order(String orderID, String email, BigDecimal amount) {
        this.orderID = orderID;
        this.email = email;
        this.amount = amount;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String toString() {
        return "[Order ID: " + orderID
                + " | Email: " + email
                + " | Amount: " + amount.setScale(2, BigDecimal.ROUND_HALF_EVEN)
                + "]";
    }

}
