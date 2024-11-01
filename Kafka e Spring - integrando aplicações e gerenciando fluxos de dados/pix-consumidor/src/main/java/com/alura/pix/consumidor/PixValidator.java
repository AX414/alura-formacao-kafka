package com.alura.pix.consumidor;

import com.alura.pix.dto.PixDTO;
import com.alura.pix.dto.PixStatus;
import com.alura.pix.exception.KeyNotFoundException;
import com.alura.pix.model.Key;
import com.alura.pix.model.Pix;
import com.alura.pix.repository.KeyRepository;
import com.alura.pix.repository.PixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class PixValidator {

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private PixRepository pixRepository;

    //é necessário criar esse tópico com 3 partições e rodar dois grupos (1 e 2),
    //é necessário mudar as portas nas propriedades da aplicação (8081 e 8082),
    //uma mudança para cada configuração de run que fizer
    @KafkaListener(topics = "pix-topic-partitions", groupId = "grupo-1")

    //Retentativa
    @RetryableTopic(
            //quanto tempo depois vai ocorrer a retentativa de envio?
            backoff = @Backoff(value=300),
            //quantas tentativas de retentativa? o comum é 3
            attempts = "10",
            //recria os tópicos para cada tentativa
            autoCreateTopics = "true",
            //quais exceptions serão incluídas?
            include = KeyNotFoundException.class
    )
    public void processaPix(PixDTO pixDTO, Acknowledgment acknowledgment) {
        //Informo o commit do processamento da mensagem, pode ser no início do código, meio ou fim.
        acknowledgment.acknowledge();
        System.out.println("Pix  recebido: " + pixDTO.getIdentifier());

        Pix pix = pixRepository.findByIdentifier(pixDTO.getIdentifier());

        Key origem = keyRepository.findByChave(pixDTO.getChaveOrigem());
        Key destino = keyRepository.findByChave(pixDTO.getChaveDestino());

        if (origem == null || destino == null) {
            pix.setStatus(PixStatus.ERRO);
            throw new KeyNotFoundException();
        } else {
            pix.setStatus(PixStatus.PROCESSADO);
        }

        pixRepository.save(pix);
    }

}
