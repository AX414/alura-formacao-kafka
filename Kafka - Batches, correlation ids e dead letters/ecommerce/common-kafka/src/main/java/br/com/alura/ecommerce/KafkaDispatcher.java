package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;

    KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    //Propriedades do producer
    private static Properties properties() {
        var properties = new Properties();
        //Servidor
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //Tanto a chave, quanto o valor, vão transformar-se em strings,
        //logo eu passo serializadores de strings para bytes
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        //Quantos são os Oks do servidor que eu quero ter? 'all' significa que quero esperar que todas as réplicas tenham essa info
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    void send(String topic, String key, T payload) throws ExecutionException, InterruptedException {
        var value = new Message<>(new CorrelationId(), payload);
        //O nome do tópico é passado primeiro, mas há diversas variações de ProducerRecord
        var record = new ProducerRecord<>(topic, key, value);
        //Envia a mensagem, o tempo que a mensagem é retida depende da configuração do servidor
        //Adicionando o callback para ter a mensagem de sucesso ou falha
        Callback callback = (data, ex) -> {
            if (ex != null) {
                System.out.println(ANSI_RED + "\nERRO.: Erro no envio da mensagem: ");
                ex.printStackTrace();
                return;
            }
            System.out.println(
                    ANSI_GREEN + "\n.:MENSAGEM ENVIADA:.\n_________________________________________"
                            + ANSI_YELLOW + "\nTópico: " + ANSI_RESET + data.topic()
                            + ANSI_YELLOW + "\nPartição: " + ANSI_RESET + data.partition()
                            + ANSI_YELLOW + "\nOffset: " + ANSI_RESET + data.offset()
                            + ANSI_YELLOW + "\nTimeStamp: " + ANSI_RESET + GeneralFunctions.formatar(data.timestamp())
                            + ANSI_YELLOW + "\nConteúdo: " + ANSI_RESET + value.toString()
                            + ANSI_GREEN + "\n_________________________________________"
            );
        };
        producer.send(record, callback).get();
    }

    @Override
    public void close() throws IOException {
        producer.close();
    }
}


