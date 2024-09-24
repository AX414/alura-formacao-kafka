package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.regex.Pattern;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class LogService {

    public static void main(String[] args) {
        var consumer = new KafkaConsumer<String, String>(properties());
        //Se inscreve no tópico do ecommerse e utiliza uma expressão regular
        consumer.subscribe(Pattern.compile("ECOMMERCE.*"));
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println(ANSI_YELLOW + "\nAVISO.: Encontrei  um novo registro agora: " + GeneralFunctions.formatar(LocalDateTime.now()));
                for (var record : records) {
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
        }
    }

    private static Properties properties() {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, LogService.class.getSimpleName());
        return properties;
    }
}
