package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class FraudDetectorService {

    public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        try (var service = new KafkaService<>(
                FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse,
                Order.class,
                Map.of())) {
            service.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println(
                ANSI_GREEN + "\n.:MENSAGEM RECEBIDA - "
                        + ANSI_YELLOW + " CHECANDO FRAUDE"
                        + ANSI_GREEN + ":.\n_________________________________________"
                        + ANSI_YELLOW + "\nPartição: " + ANSI_RESET + record.partition()
                        + ANSI_YELLOW + "\nOffset: " + ANSI_RESET + record.offset()
                        + ANSI_YELLOW + "\nTimeStamp: " + ANSI_RESET + GeneralFunctions.formatar(record.timestamp())
                        + ANSI_YELLOW + "\nConteúdo: " + ANSI_RESET + record.value()
                        + ANSI_GREEN + "\n_________________________________________"
        );

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var message = record.value();
        var order = message.getPayload();
        if (isFraud(order)) {
            //Fingindo que a fraude ocorre se o valor é maior que 4500

            System.out.println(ANSI_RED + "Fraude Detectada.");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED",
                    order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        } else {
            System.out.println(ANSI_GREEN + "Nenhuma fraude detectada. Pedido processado.");
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED",
                    order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()), order);
        }
    }

    private static boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
