package com.iprody.paymentservice.xpaymentadapterapp.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import com.iprody.paymentservice.xpaymentadapterapp.async.AsyncListener;
import com.iprody.paymentservice.xpaymentadapterapp.async.MessageHandler;
import com.iprody.paymentservice.xpaymentadapterapp.async.XPaymentAdapterRequestMessage;

@Slf4j
@Component
public class KafkaXPaymentAdapterRequestListenerAdapter
        implements AsyncListener<XPaymentAdapterRequestMessage> {

    private final MessageHandler<XPaymentAdapterRequestMessage> handler;

    public KafkaXPaymentAdapterRequestListenerAdapter(MessageHandler<XPaymentAdapterRequestMessage> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.x-payment-adapter.request}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(
            ConsumerRecord<String, XPaymentAdapterRequestMessage> record,
            Acknowledgment ack
    ) {

        XPaymentAdapterRequestMessage message = record.value();
        try {
            log.info("Received XPayment Adapter request: paymentGuid={}, partition={}, offset={}",
                    message.getPaymentGuid(), record.partition(), record.offset());
            onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter request for paymentGuid={}", message.getPaymentGuid(), e);
            throw e;
        }
    }
}
