package br.com.alura.ecommerce;

public class Message<T> {

    private final CorrelationId id;
    private final T payload;

    //conteúdo/payload é do tipo T pois pode ser qualquer coisa
    public Message(CorrelationId id, T payload){
        this.id = id;
        this.payload = payload;
    }

    public CorrelationId getId() {
        return id;
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Message["
                + " ID: " + id
                + " | Payload: " + payload
                + " ]";
    }

}

