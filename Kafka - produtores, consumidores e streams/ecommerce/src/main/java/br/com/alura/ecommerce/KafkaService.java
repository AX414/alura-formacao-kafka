package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static br.com.alura.ecommerce.GeneralFunctions.ANSI_YELLOW;

public class KafkaService {
    private final KafkaConsumer<String, String> consumer;
    private final ConsumerFunction parse;

     KafkaService(String groupID, String topic, ConsumerFunction parse){
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(properties(groupID));
        consumer.subscribe(Collections.singleton(topic));
    }

     void run(){
        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));
            if (!records.isEmpty()) {
                System.out.println(ANSI_YELLOW + "\nAVISO.: Encontrei  um novo registro agora: " + GeneralFunctions.formatar(LocalDateTime.now()));
                for (var record : records) {
                    parse.consume(record);
                }
            }
        }
    }

    //Propriedades do consumer
    private static Properties properties(String groupID) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //Producer serializa(transforma de string para byte)
        //Consumer desserializa(transforma de byte para string)
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        return properties;
    }
}
