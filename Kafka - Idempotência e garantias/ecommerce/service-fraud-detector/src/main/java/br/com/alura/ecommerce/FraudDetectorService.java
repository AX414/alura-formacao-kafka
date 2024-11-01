package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.database.LocalDatabase;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class FraudDetectorService implements ConsumerService<Order> {

    private final LocalDatabase database;

    FraudDetectorService() throws SQLException{
        this.database = new LocalDatabase("frauds_database");
        this.database.createIfNotExists("create table Orders(" +
                "uuid varchar(50) primary key," +
                "is_fraud boolean" +
                ")");
    }

    public static void main(String[] args) {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println(
                ANSI_GREEN + "\n.:MENSAGEM RECEBIDA - "
                        + ANSI_YELLOW + " CHECANDO FRAUDE"
                        + ANSI_GREEN + ":.\n_________________________________________"
                        + ANSI_YELLOW + "\nPartição: " + ANSI_RESET + record.partition()
                        + ANSI_YELLOW + "\nOffset: " + ANSI_RESET + record.offset()
                        + ANSI_YELLOW + "\nTimeStamp: " + ANSI_RESET + GeneralFunctions.formatar(record.timestamp())
                        + ANSI_YELLOW + "\nConteúdo: " + ANSI_RESET + record.value().getPayload()
                        + ANSI_GREEN + "\n_________________________________________"
        );


        var message = record.value();
        var order = message.getPayload();

        if(wasProcessed(order)){
            System.out.println("\nA Ordem "+order.getOrderID()+" já foi processada.");
            return;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (isFraud(order)) {
            //Fingindo que a fraude ocorre se o valor é maior que 4500
            database.update("insert into Orders(uuid, is_fraud) values (?, true)", order.getOrderID());

            System.out.println(ANSI_RED + "Fraude Detectada.");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED",
                    order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        } else {
            database.update("insert into Orders(uuid, is_fraud) values (?, false)", order.getOrderID());

            System.out.println(ANSI_GREEN + "Nenhuma fraude detectada. Pedido processado.");
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED",
                    order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        }
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var results = database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderID());
        return results.next();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudDetectorService.class.getSimpleName();
    }

    private static boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
