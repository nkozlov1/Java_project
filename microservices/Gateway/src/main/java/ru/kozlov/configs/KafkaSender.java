package ru.kozlov.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.kozlov.exeptions.KafkaSyncRequestException;
import ru.kozlov.responses.KafkaResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class KafkaSender {
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafka;
    private final KafkaTemplate<String, Object> kafka;

    public KafkaSender(ReplyingKafkaTemplate<String, Object, Object> replyingKafka,
                       @Qualifier("kafkaTemplate") KafkaTemplate<String, Object> kafka) {
        this.replyingKafka = replyingKafka;
        this.kafka = kafka;
    }

    public <T> KafkaResponse<T> sendSync(Object request, String topic, String service, Class<T> awaitedType) {
        final ConsumerRecord<String, Object> consumerRecord;
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, request);
            RequestReplyFuture<String, Object, Object> replyFuture = replyingKafka.sendAndReceive(record);
            consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new KafkaSyncRequestException("Ошибка при отправке синхронного запроса в " + service +
                    ": " + e.getMessage());
        }
        Object reply = consumerRecord.value();
        if (reply instanceof KafkaResponse) {
            log.info(service + " " + ((KafkaResponse<?>) reply).getData().toString());
            return (KafkaResponse<T>) reply;
        }
        throw new KafkaSyncRequestException(
                String.format("Получен неожиданный тип ответа от %s. Ожидался %s, получен %s",
                        service, awaitedType.getSimpleName(), reply.getClass().getSimpleName())
        );
    }

    public void sendAsync(Object request, String topic) {
        kafka.send(topic, request);
    }
}
