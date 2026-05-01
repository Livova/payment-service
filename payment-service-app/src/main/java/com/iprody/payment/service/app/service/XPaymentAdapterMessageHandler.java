package com.iprody.payment.service.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.iprody.payment.service.app.async.MessageHandler;
import com.iprody.payment.service.app.async.XPaymentAdapterResponseMessage;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;

@Slf4j
@Service
public class XPaymentAdapterMessageHandler implements MessageHandler<XPaymentAdapterResponseMessage> {

    private final PaymentService paymentService;

    public XPaymentAdapterMessageHandler(@Lazy PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        log.info("Received message {} ", message);
        log.info("Received message with id {} and status {}", message.getMessageGuid(), message.getStatus());
        final PaymentStatus newStatus = switch (message.getStatus()) {
            case SUCCEEDED -> PaymentStatus.APPROVED;
            case PROCESSING -> PaymentStatus.PENDING;
            case CANCELED -> PaymentStatus.DECLINED;
        };

        paymentService.updateStatus(message.getPaymentGuid(), newStatus);
    }
}
