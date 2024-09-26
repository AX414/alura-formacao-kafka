package br.com.alura.ecommerce;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var dispatcher = new KafkaDispatcher()) {
            for (var i = 0; i < 10; i++) {
                var key = UUID.randomUUID().toString();
                var value = "UUID[" + key + "]";
                dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);

                var email = "Bem-vindo! Estamos processando o seu pedido!"
                        + ANSI_YELLOW + "\nPedido: " + ANSI_RESET + value;
                dispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
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
