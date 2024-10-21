package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class EmailNewOrderService {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var enoService = new EmailNewOrderService();
        try (var service = new KafkaService<>(
                EmailNewOrderService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                enoService::parse,
                Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println(
                ANSI_GREEN + "\n.:MENSAGEM RECEBIDA - "
                        + ANSI_YELLOW + " PREPARANDO EMAIL"
                        + ANSI_GREEN + ":.\n_________________________________________"
                        + ANSI_YELLOW + "\nPartição: " + ANSI_RESET + record.partition()
                        + ANSI_YELLOW + "\nOffset: " + ANSI_RESET + record.offset()
                        + ANSI_YELLOW + "\nTimeStamp: " + ANSI_RESET + GeneralFunctions.formatar(record.timestamp())
                        + ANSI_YELLOW + "\nConteúdo: " + ANSI_RESET + record.value().getPayload()
                        + ANSI_GREEN + "\n_________________________________________"
        );

        var msg = record.value();
        Order order = msg.getPayload();
        CorrelationId id = msg.getId().continueWith(EmailNewOrderService.class.getSimpleName());

        var emailTemplate = "Bem-vindo! Estamos processando o seu pedido!"
                + ANSI_YELLOW + "\nPedido: " + ANSI_RESET + order.getOrderID();
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), id, emailTemplate);

    }

}
