package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class CreateUserService implements ConsumerService<Order> {

    private final Connection connection;

    private CreateUserService() throws SQLException {
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

    public static void main(String[] args) {
        new ServiceRunner<>(CreateUserService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws Exception {
        System.out.println(
                ANSI_GREEN + "\n.:MENSAGEM RECEBIDA:."
                        + ANSI_GREEN + "\n_________________________________________"
                        + ANSI_YELLOW + "\nConteúdo: " + ANSI_RESET + record.value()
                        + ANSI_GREEN + "\n_________________________________________"
                        + ANSI_GREEN + "\nProcessando ordem, checando por novo usuário."
        );
        var order = record.value().getPayload();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    private void insertNewUser(String email) throws SQLException {
        var uuid = UUID.randomUUID().toString();
        System.out.println("Generated UUID: " + uuid);
        var insert = connection.prepareStatement("insert into Users (uuid, email) values (?, ?)");
        insert.setString(1, uuid);
        insert.setString(2, email);
        insert.execute();
        System.out.println(ANSI_GREEN + "\nUsuário adicionado ao banco de dados.");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("select uuid from Users where email = ? limit 1");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }

}
