package com.iprody.paymentservice.xpaymentadapterapp.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import com.iprody.paymentservice.xpaymentadapterapp.async.AsyncSender;
import com.iprody.paymentservice.xpaymentadapterapp.async.XPaymentAdapterResponseMessage;

@Service
public class KafkaXPaymentAdapterResponseSender
        implements AsyncSender<XPaymentAdapterResponseMessage> {

    private static final Logger log = LoggerFactory.getLogger(KafkaXPaymentAdapterResponseSender.class);

    private final KafkaTemplate<String, XPaymentAdapterResponseMessage> template;

    private final String topic;

    public KafkaXPaymentAdapterResponseSender(
            KafkaTemplate<String, XPaymentAdapterResponseMessage> template,
            @Value("${app.kafka.topics.x-payment-adapter.response}") String topic
    ) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void send(XPaymentAdapterResponseMessage msg) {
        String key = msg.getPaymentGuid().toString(); // фиксируем партиционирование по платежу
        log.info("Sending XPayment Adapter response: guid={}, amount={}, currency={} -> topic={}",
                msg.getPaymentGuid(), msg.getAmount(), msg.getCurrency(), topic);
        var future = template.send(topic, key, msg);
        try {
            SendResult<String, XPaymentAdapterResponseMessage> result = future.get();
            if (result != null && result.getRecordMetadata() != null) {
                log.info("Message sent to topic {} partition {} offset {}",
                        result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                log.info("Message sent to topic {} (no metadata)", topic);
            }
        } catch (Exception ex) {
            log.error("Failed to send message to topic {}: {}", topic, ex.getMessage(), ex);
        }
    }
}
