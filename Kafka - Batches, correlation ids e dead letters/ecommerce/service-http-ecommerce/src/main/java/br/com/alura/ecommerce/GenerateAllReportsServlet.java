package br.com.alura.ecommerce;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class GenerateAllReportsServlet extends HttpServlet {

    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            batchDispatcher.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            batchDispatcher.send("SEND_MESSAGE_TO_ALL_USERS", "USER_GENERATE_READING_REPORT", "USER_GENERATE_READING_REPORT" );

            System.out.println(ANSI_GREEN+"Enviando relatório para todos os usuários.");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Relatório foi gerado com sucesso.");
        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }

    }

}
