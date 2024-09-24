package br.com.alura.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //Tipo da chave, tipo da Mensagem
        var producer = new KafkaProducer<String, String>(properties());
        var value = UUID.randomUUID().toString();
        //O nome do tópico é passado primeiro, mas há diversas variações de ProducerRecord
        var record = new ProducerRecord<>("ECOMMERCE_NEW_ORDER", value, value);
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
                            + ANSI_YELLOW + "\nConteúdo: " + ANSI_RESET + record.value()
                            + ANSI_GREEN + "\n_________________________________________"
            );
        };


        var email="Bem-vindo! Estamos processando o seu pedido!"
                +ANSI_YELLOW+"\nPedido: "+ANSI_RESET+record.value();
        var emailRecord = new ProducerRecord<>("ECOMMERCE_SEND_EMAIL", email,email);
        producer.send(emailRecord, callback).get();
        producer.send(record, callback).get();
    }

    //Propriedades do producer
    private static Properties properties() {
        var properties = new Properties();
        //Servidor
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //Tanto a chave, quanto o valor, vão transformar-se em strings,
        //logo eu passo serializadores de strings para bytes
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }
}
