package br.com.alura.ecommerce.consumer;

import java.util.concurrent.Executors;

public class ServiceRunner<T> {

    private final ServiceProvider<T> provider;

    public ServiceRunner(ServiceFactory<T> factory) {
        this.provider = new ServiceProvider<>(factory);
    }

    public void start(int threadcount) {
        var pool = Executors.newFixedThreadPool(threadcount);
        for (int i = 0; i <= threadcount; i++) {
            pool.submit(provider);
        }
    }
}
