package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class NewOrderMain {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {
            try (KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>()) {

                for (var i = 0; i < 10; i++) {
                    var orderID = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    var emailAleatorio = UUID.randomUUID()+"@hotmail.com";

                    var order = new Order(orderID, amount, emailAleatorio);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", emailAleatorio, new CorrelationId(NewOrderMain.class.getSimpleName()), order);

                    var emailTemplate = "Bem-vindo! Estamos processando o seu pedido!"
                            + ANSI_YELLOW + "\nPedido: " + ANSI_RESET + orderID;
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", emailAleatorio, new CorrelationId(NewOrderMain.class.getSimpleName()), emailTemplate);
                }
            }
        } catch (Exception e) {
            /*
            Com a função do close, o dispatcher sempre será
            fechado, independente de sucesso ou não,
            o recurso será fechado.
             */
            e.printStackTrace();
        }
    }
}
