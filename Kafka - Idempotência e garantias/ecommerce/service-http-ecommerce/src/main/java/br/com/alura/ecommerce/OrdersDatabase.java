package br.com.alura.ecommerce;

import br.com.alura.ecommerce.database.LocalDatabase;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class OrdersDatabase implements Closeable {

    private final LocalDatabase database;

    OrdersDatabase() throws SQLException {
        this.database = new LocalDatabase("frauds_database");
        this.database.createIfNotExists("create table Orders(" + "uuid varchar(50) primary key)");
    }

    public boolean saveNewOrder(Order order) throws SQLException {
        if (wasProcessed(order)) {
            return false;
        } else {
            this.database.update("insert into Orders(uuid) values(?)", order.getOrderID());
            return true;
        }
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderID());
        return results.next();
    }

    @Override
    public void close() throws IOException {
        try {
            database.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
