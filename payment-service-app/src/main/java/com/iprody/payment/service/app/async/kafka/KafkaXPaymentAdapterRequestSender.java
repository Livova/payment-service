package com.iprody.payment.service.app.async.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.iprody.payment.service.app.async.AsyncSender;
import com.iprody.payment.service.app.async.XPaymentAdapterRequestMessage;

@Slf4j
@Service
public class KafkaXPaymentAdapterRequestSender
    implements AsyncSender<XPaymentAdapterRequestMessage> {

    // Use Object as the value type so it matches the auto-configured KafkaTemplate bean
    private final KafkaTemplate<String, Object> template;

    private final String topic;

    @Autowired
    public KafkaXPaymentAdapterRequestSender(
        KafkaTemplate<String, Object> template,
        @Value("${app.kafka.topics.xpayment-adapter.request:xpayment-adapter.requests}") String topic
    ) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage msg) {
        final String key = msg.getPaymentGuid().toString(); // фиксируем партиционирование по платежу
        log.info("Sending XPayment Adapter request: guid={}, amount={}, currency={} -> topic={}",
            msg.getPaymentGuid(), msg.getAmount(), msg.getCurrency(), topic);
        // template accepts Object values now
        template.send(topic, key, msg);
    }
}
