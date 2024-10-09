package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String orderID;
    private final BigDecimal amount;

    public Order(String orderID, BigDecimal amount) {
        this.orderID = orderID;
        this.amount = amount;
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
                + " | Amount: " + amount.setScale(2, BigDecimal.ROUND_HALF_EVEN)
                + "]";
    }

    public String getEmail() {
        return "";
    }
}
