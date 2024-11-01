package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class NewOrderMain {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {
            for (var i = 0; i < 10; i++) {
                var orderID = UUID.randomUUID().toString();
                var amount = new BigDecimal(Math.random() * 5000 + 1);
                var emailAleatorio = UUID.randomUUID() + "@hotmail.com";

                var id = new CorrelationId(NewOrderMain.class.getSimpleName());
                var order = new Order(orderID, amount, emailAleatorio);
                orderDispatcher.send("ECOMMERCE_NEW_ORDER", emailAleatorio, id, order);
            }
        }
    }
}
