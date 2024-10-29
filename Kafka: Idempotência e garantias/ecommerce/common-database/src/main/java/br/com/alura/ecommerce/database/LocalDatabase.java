package br.com.alura.ecommerce.database;

import javax.xml.transform.Result;
import java.io.File;
import java.sql.*;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/" + name + ".db";
        this.connection = DriverManager.getConnection(url);
        this.connection.setAutoCommit(true);

    }

    //  genérico demais, isso permite database injections.
    public void createIfNotExists(String sql) {
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PreparedStatement getPreparedStatement(String statement, String[] params) throws SQLException {
        var preparedStatement = connection.prepareStatement(statement);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        return preparedStatement;
    }

    public void update(String statement, String... params) throws SQLException {
        getPreparedStatement(statement, params).execute();
    }

    public ResultSet query(String query, String... params) throws SQLException {
        return getPreparedStatement(query, params).executeQuery();
    }
}
