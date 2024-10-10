package br.com.alura.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class IO {
    public static void copyTo(Path source, File target) throws IOException {
        target.getParentFile().mkdirs();
        //Se o relatório já existir, você usa o replace para sobrescrever ele
        Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void append(File target, String content) {
        try {
            Files.write(target.toPath(), content.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
