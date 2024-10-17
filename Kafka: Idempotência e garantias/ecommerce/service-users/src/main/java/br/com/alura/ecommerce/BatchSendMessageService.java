package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class BatchSendMessageService {

    private final Connection connection;

    BatchSendMessageService() throws SQLException {
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

    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException, IOException {
        var batchService = new BatchSendMessageService();
        try (var service = new KafkaService<>(
                BatchSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                batchService::parse,
                Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException {
        var message = record.value();
        System.out.println(
                ANSI_GREEN + "\n.:PROCESSANDO BATCH NOVA:."
                        + ANSI_GREEN + "\n_________________________________________"
                        + ANSI_YELLOW + "\nTópico: " + ANSI_RESET + message.getPayload()
        );

        if (true) throw new RuntimeException(ANSI_RED + "\nErro intencional.");

        for (User u : getAllUsers()) {
            userDispatcher.sendAsync(message.getPayload(),
                    u.getUuid(),
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()), u);
            System.out.println(ANSI_YELLOW + "\nEnviado para o user: " + ANSI_RESET + u);
        }

        System.out.println(ANSI_GREEN + "\n_________________________________________"
                + ANSI_GREEN + "\nProcessando ordem, checando por novo usuário."

        );

    }

    private List<User> getAllUsers() throws SQLException {
        var results = connection.prepareStatement("select uuid from Users").executeQuery();

        List<User> users = new ArrayList<>();
        while (results.next()) {
            users.add(new User(results.getString(1)));
        }
        return users;
    }

}
