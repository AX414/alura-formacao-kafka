package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        var orderID = req.getParameter("uuid");

        String emailDoParametro = req.getParameter("email");
        BigDecimal amount;

        // Se o email não for fornecido, gera um novo email aleatório
        if (emailDoParametro == null || emailDoParametro.isBlank()) {
            emailDoParametro = UUID.randomUUID().toString() + "@hotmail.com";
        }

        // Se o valor não for fornecido, define um valor padrão
        String amountParam = req.getParameter("amount");
        if (amountParam == null || amountParam.isBlank()) {
            amount = BigDecimal.valueOf(Math.random() * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            amount = new BigDecimal(amountParam);
        }

        var order = new Order(orderID, amount, emailDoParametro);


        try(var database = new OrdersDatabase()) {
            if (database.saveNewOrder(order)) {
                orderDispatcher.send("ECOMMERCE_NEW_ORDER", emailDoParametro, new CorrelationId(NewOrderServlet.class.getSimpleName()), order);
                System.out.println(ANSI_GREEN + "\nNova ordem processada.");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("\nNova ordem enviada.");
            } else {
                System.out.println(ANSI_RED + "\nOrdem antiga recebida.");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("\nOrdem antiga recebida.");
            }
        } catch (SQLException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
