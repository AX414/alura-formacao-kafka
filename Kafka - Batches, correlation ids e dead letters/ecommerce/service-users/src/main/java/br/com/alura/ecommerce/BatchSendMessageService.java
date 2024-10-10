package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    public static void main(String[] args) throws SQLException {
        var batchService = new BatchSendMessageService();
        try (var service = new KafkaService<>(
                BatchSendMessageService.class.getSimpleName(),
                "SEND_MESSAGE_TO_ALL_USERS",
                batchService::parse,
                String.class,
                Map.of())) {
            service.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<User>();

    private void parse(ConsumerRecord<String, String> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println(
                ANSI_GREEN + "\n.:PROCESSANDO BATCH NOVA:."
                        + ANSI_GREEN + "\n_________________________________________"
                        + ANSI_YELLOW + "\nTópico: " + ANSI_RESET + record.topic()
        );

        for (User u : getAllUsers()) {
            userDispatcher.send(record.value(), u.getUuid(), u);
        }

        System.out.println(ANSI_GREEN + "\n_________________________________________"
                + ANSI_GREEN + "\nProcessando ordem, checando por novo usuário."

        );

    }

    private List<User> getAllUsers() throws SQLException{
        var results = connection.prepareStatement("select uuid from Users").executeQuery();

        List<User> users = new ArrayList<>();
        while(results.next()){
            users.add(new User(results.getString(1)));
        }
        return users;
    }

}
