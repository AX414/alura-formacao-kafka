package br.com.alura.ecommerce;

import java.math.BigDecimal;


public class User {
    private final String uuid;
    private final String email;

    public User(String uuid, String email) {
        this.uuid = uuid;
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "[String: " + uuid
                + " | Email: " + email
                + "]";
    }

    public String getReportPath() {
        return "target/" + uuid + "-report.txt";
    }
}
