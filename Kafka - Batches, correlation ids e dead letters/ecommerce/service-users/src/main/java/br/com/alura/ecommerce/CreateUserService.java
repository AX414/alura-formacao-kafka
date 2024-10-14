package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        File dbFile = new File(url.replace("jdbc:sqlite:", ""));
        dbFile.getParentFile().mkdirs(); // Cria os diretórios se não existem
        this.connection = DriverManager.getConnection(url);
        this.connection.setAutoCommit(true);
        try {
            connection.createStatement().execute("create table if not exists Users(" +
                    "uuid VARCHAR(200) primary key, " +
                    "email VARCHAR(200))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try (var service = new KafkaService<>(
                CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                Order.class,
                Map.of())) {
            service.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println(
                ANSI_GREEN + "\n.:MENSAGEM RECEBIDA:."
                        + ANSI_GREEN + "\n_________________________________________"
                        + ANSI_YELLOW + "\nConteúdo: " + ANSI_RESET + record.value().getPayload()
                        + ANSI_GREEN + "\n_________________________________________"
                        + ANSI_GREEN + "\nProcessando ordem, checando por novo usuário."

        );

        var message = record.value();
        var order = message.getPayload();
        try {
            if (isNewUser(order.getEmail())) {
                try {
                    insertNewUser(order.getEmail());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println(ANSI_RED + "\nUsuário já existia no banco de dados.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void insertNewUser(String email) throws SQLException {
        var uuid = UUID.randomUUID().toString();
        System.out.println("Generated UUID: " + uuid);
        var insert = connection.prepareStatement("insert into Users (uuid, email) values (?, ?)");
        insert.setString(1, uuid);
        insert.setString(2, email);

        insert.execute();
        insert.close(); // Fechar o PreparedStatement

        System.out.println(ANSI_GREEN+"\nUsuário adicionado ao banco de dados.");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("select uuid from Users where email = ? limit 1");
        exists.setString(1, email);

        var results = exists.executeQuery();
        boolean userExists = results.next();
        exists.close(); // Fechar o PreparedStatement

        System.out.println(ANSI_YELLOW+"\nUsuário existe: " + userExists);
        return !userExists;
    }

}
