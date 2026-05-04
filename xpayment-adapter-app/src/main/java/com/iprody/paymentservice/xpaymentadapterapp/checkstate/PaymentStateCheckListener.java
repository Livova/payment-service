package com.iprody.paymentservice.xpaymentadapterapp.checkstate;

import com.iprody.paymentservice.xpaymentadapterapp.dto.PaymentCheckStateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.iprody.paymentservice.xpaymentadapterapp.checkstate.RabitMqDlxConfig.DEAD_LETTER_EXCHANGE;
import static com.iprody.paymentservice.xpaymentadapterapp.checkstate.RabitMqDlxConfig.DEAD_LETTER_ROUTING_KEY;

@Slf4j
@Component
public class PaymentStateCheckListener {

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;
    private final PaymentStateCheckHandler paymentStatusCheckHandler;

    @Value("${app.rabbitmq.max-retries:60}")
    private int maxRetries;

    @Value("${app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @Autowired
    public PaymentStateCheckListener(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.delayed-exchange-name}") String exchangeName,
            @Value("${app.rabbitmq.queue-name}") String routingKey,
            PaymentStateCheckHandler paymentStatusCheckHandler
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.paymentStatusCheckHandler = paymentStatusCheckHandler;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        log.info("Received payment state check message for chargeGuid={}", message.chargeGuid());

        boolean paid = paymentStatusCheckHandler.handle(message.chargeGuid());
        if (paid) {
            return;
        }

        MessageProperties props = raw.getMessageProperties();
        int retryCount = (int) props.getHeaders().getOrDefault("x-retry-count", 0);

        if (retryCount < maxRetries) {
            log.info("Payment is not paid yet. Retrying in {} ms (attempt {}/{})",
                    intervalMs, retryCount + 1, maxRetries);

            // Планируем следующую проверку
            PaymentCheckStateMessage newMessage = new PaymentCheckStateMessage(
                    message.chargeGuid(),
                    message.paymentGuid(),
                    message.amount(),
                    message.currency()
            );

            rabbitTemplate.convertAndSend(
                    exchangeName,
                    routingKey,
                    newMessage,
                    m -> {
                        m.getMessageProperties().setHeader("x-delay", intervalMs);
                        m.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
                        return m;
                    }
            );
        } else {
            // Исчерпали попытки -- кладём сообщение в DLX
            rabbitTemplate.convertAndSend(
                    DEAD_LETTER_EXCHANGE,
                    DEAD_LETTER_ROUTING_KEY,
                    message,
                    m -> {
                        m.getMessageProperties().setHeader("x-retry-count", retryCount);
                        m.getMessageProperties().setHeader("x-final-status", "TIMEOUT");
                        m.getMessageProperties().setHeader("x-original-queue", props.getConsumerQueue());
                        return m;
                    }
            );
        }
    }
}


