package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.KafkaService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class ReadingReportService implements ConsumerService<User> {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(ReadingReportService::new).start(5);
    }

    public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        System.out.println(ANSI_GREEN + "\n.:Gerando relatório:."
                + ANSI_GREEN + ":.\n_________________________________________");

        var message = record.value();
        var user = message.getPayload();
        var target = new File(user.getReportPath());

        IO.copyTo(SOURCE, target);
        IO.append(target, "\nCriado para o usuário: " + user.getUuid());
        System.out.println(ANSI_YELLOW + "Arquivo criado em: " + ANSI_RESET + target.getAbsolutePath());
        System.out.println(ANSI_GREEN + "_________________________________________");
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_USER_GENERATE_READING_REPORT";
    }

    @Override
    public String getConsumerGroup() {
        return ReadingReportService.class.getSimpleName();
    }
}
