package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class GeneralFunctions {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    // Função genérica que aceita qualquer tipo de objeto
    public static <T> String formatar(T input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

        if (input instanceof LocalDateTime) {
            // Se for um LocalDateTime, formata diretamente
            return ((LocalDateTime) input).format(formatter);
        } else if (input instanceof Long) {
            // Se for um Long (milissegundos), converte para LocalDateTime
            LocalDateTime localDateTime = Instant.ofEpochMilli((Long) input)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            return localDateTime.format(formatter);
        } else {
            throw new IllegalArgumentException("Tipo de entrada não suportado: " + input.getClass());
        }
    }

}
