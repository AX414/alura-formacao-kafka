package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                for (var i = 0; i < 10; i++) {

                    var userID = UUID.randomUUID().toString();
                    var orderID = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    var order = new Order(userID, orderID, amount);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", userID, order);

                    var email = "Bem-vindo! Estamos processando o seu pedido!"
                            + ANSI_YELLOW + "\nPedido: " + ANSI_RESET + orderID;
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userID, email);
                }
            }
        }catch (Exception e) {
            /*
            Com a função do close, o dispatcher sempre será
            fechado, independente de sucesso ou não,
            o recurso será fechado.
             */
            e.printStackTrace();
        }
    }
}
