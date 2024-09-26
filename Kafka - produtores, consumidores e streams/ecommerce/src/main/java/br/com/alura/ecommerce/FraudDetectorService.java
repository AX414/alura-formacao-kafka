package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class FraudDetectorService {

    public static void main(String[] args) {
        var fraudService = new FraudDetectorService();
        try (var service = new KafkaService(
                FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudService::parse)) {
            service.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
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
        System.out.println("Pedido processado.");
    }

}
