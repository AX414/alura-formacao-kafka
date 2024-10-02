package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String userID, orderID;
    private final BigDecimal amount;

    public Order(String userID, String orderID, BigDecimal amount) {
        this.userID = userID;
        this.orderID = orderID;
        this.amount = amount;
    }

    public String getUserID() {
        return userID;
    }

    public String getOrderID() {
        return orderID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "[Order ID: " + orderID
                + " | User ID: " + userID
                + " | Amount: " + amount.setScale(2, BigDecimal.ROUND_HALF_EVEN)
                + "]";
    }

    public String getEmail() {
        return "";
    }
}
