package br.com.alura.ecommerce;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.Source;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static br.com.alura.ecommerce.GeneralFunctions.*;

public class NewOrderServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>()) {
                try {

                    var orderID = UUID.randomUUID().toString();

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
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", emailDoParametro, order);

                    var emailTemplate = "Bem-vindo! Estamos processando o seu pedido!"
                            + ANSI_YELLOW + "\nPedido: " + ANSI_RESET + orderID;
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", emailDoParametro, emailTemplate);

                    System.out.println(ANSI_GREEN + "\nNova ordem processada.");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("\nNova ordem enviada.");
                }  catch (ExecutionException | InterruptedException e) {
                    throw new ServletException(e);
                }

            }
        } catch (Exception e) {
            /*
            Com a função do close, o dispatcher sempre será
            fechado, independente de sucesso ou não,
            o recurso será fechado.
             */
            e.printStackTrace();
        }
    }
}
