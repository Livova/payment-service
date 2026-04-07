package com.iprody.payment.service.app.async;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@Service
class InMemoryXPaymentAdapterMessageBroker
    implements AsyncSender<XPaymentAdapterRequestMessage> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AsyncListener<XPaymentAdapterResponseMessage> resultListener;

    @Autowired
    public InMemoryXPaymentAdapterMessageBroker(
        AsyncListener<XPaymentAdapterResponseMessage> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage request) {
        final UUID txId = UUID.randomUUID();
        scheduler.schedule(() -> emit(request, txId, XPaymentAdapterStatus.PROCESSING, true), 0,  TimeUnit.SECONDS);
        scheduler.schedule(() -> emit(request, txId, XPaymentAdapterStatus.PROCESSING, true), 10, TimeUnit.SECONDS);
        scheduler.schedule(() -> emit(request, txId, XPaymentAdapterStatus.SUCCEEDED, false), 20, TimeUnit.SECONDS);
    }

    private void emit(XPaymentAdapterRequestMessage request, UUID txId, XPaymentAdapterStatus status,
        boolean replaceStatus) {
        //добавила messageGuid а то негоже
        final UUID messageGuid = UUID.randomUUID();
        //новая переменная чтобы проставлять статус динамически
        final XPaymentAdapterResponseMessage result = new XPaymentAdapterResponseMessage();
        final XPaymentAdapterStatus finalStatus = (replaceStatus) ? status :
            ( (request.getAmount().remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) ?
            XPaymentAdapterStatus.SUCCEEDED : XPaymentAdapterStatus.CANCELED);
        result.setPaymentGuid(request.getPaymentGuid());
        result.setAmount(request.getAmount());
        result.setCurrency(request.getCurrency());
        result.setTransactionRefId(txId);
        result.setStatus(finalStatus);
        result.setOccurredAt(OffsetDateTime.now());
        result.setMessageGuid(messageGuid);
        resultListener.onMessage(result);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
