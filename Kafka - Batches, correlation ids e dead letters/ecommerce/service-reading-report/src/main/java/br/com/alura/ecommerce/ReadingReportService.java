package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class ReadingReportService {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) {
        var reportService = new ReadingReportService();
        try (var service = new KafkaService<>(
                ReadingReportService.class.getSimpleName(),
                "USER_GENERATE_READING_REPORT",
                reportService::parse,
                User.class,
                Map.of())) {
            service.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parse(ConsumerRecord<String, User> record) {
        System.out.println(ANSI_GREEN + "\n.:Gerando relatório:."
                + ANSI_GREEN + ":.\n_________________________________________");
        var user = record.value();
        var target = new File(user.getReportPath());
        try {
            IO.copyTo(SOURCE, target);
            IO.append(target, "\nCriado para o usuário: " + user.getUuid());
            System.out.println(ANSI_YELLOW+ "Arquivo criado em: "+ANSI_RESET+ target.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(ANSI_GREEN + "_________________________________________");
    }


}
