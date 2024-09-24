package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;

import static br.com.alura.ecommerce.GeneralFunctions.*;


public class FraudDetectorService {


    public static void main(String[] args) {
        var consumer = new KafkaConsumer<String, String>(properties());
        //Se inscreve no tópico do ecommerse
        consumer.subscribe(Collections.singleton("ECOMMERCE_NEW_ORDER"));

        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println(ANSI_YELLOW + "\nAVISO.: Encontrei  um novo registro agora: " + GeneralFunctions.formatar(LocalDateTime.now()));
                for (var record : records) {
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
        }
    }

    //Propriedades do consumer
    private static Properties properties() {
        var properties = new Properties();
        //Servidor
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //Producer serializa(transforma de string para byte)
        //Consumer desserializa(transforma de byte para string)
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //Precisa especificar o ID do grupo
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getSimpleName());

        return properties;
    }
}
