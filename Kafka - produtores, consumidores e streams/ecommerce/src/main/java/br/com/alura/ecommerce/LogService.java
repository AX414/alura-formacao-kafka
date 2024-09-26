package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class LogService {

    public static void main(String[] args) {
        var logService = new LogService();
        var service = new KafkaService(
                LogService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                logService::parse);
        service.run();
    }

    private void parse(ConsumerRecord<String, String> record) {
        System.out.println(
                ANSI_GREEN + "\n.:MENSAGEM RECEBIDA PARA O LOG:.\n_________________________________________"
                        + ANSI_YELLOW + "\nTópico: " + ANSI_RESET + record.topic()
                        + ANSI_YELLOW + "\nPartição: " + ANSI_RESET + record.partition()
                        + ANSI_YELLOW + "\nOffset: " + ANSI_RESET + record.offset()
                        + ANSI_YELLOW + "\nTimeStamp: " + ANSI_RESET + GeneralFunctions.formatar(record.timestamp())
                        + ANSI_YELLOW + "\nConteúdo: " + ANSI_RESET + record.value()
                        + ANSI_GREEN + "\n_________________________________________"
        );
    }

}
