package com.iprody.paymentservice.xpaymentadapterapp;

import com.iprody.paymentservice.xpaymentadapterapp.async.*;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.iprody.paymentservice.xpaymentadapterapp.async.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> sender) {
        this.sender = sender;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        // добавила проверку сюда, потому что бизнес-логика
        // но не уверена что это корректно
        if (message.getAmount() == null) {
            throw new BusinessException("Amount is required");
        }
        if (message.getCurrency() == null) {
            throw new BusinessException("Currency is required");
        }
        if (message.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Less Than Zero Amount");
        }
        final Currency currency = Currency.getInstance(message.getCurrency());
        final int expectedScale = currency.getDefaultFractionDigits();

        final int actualScale = message.getAmount().stripTrailingZeros().scale();

        if (actualScale > expectedScale) {
            throw new BusinessException(
                    String.format(
                            "Invalid scale for currency %s: expected max %d fraction digits, but got %d",
                            message.getCurrency(), expectedScale, actualScale
                    )
            );
        }

        scheduler.schedule(() -> {
            log.info("Sending response about processing request {}", message.getPaymentGuid());
            final XPaymentAdapterStatus finalStatus = (message.getAmount().remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) ?
               XPaymentAdapterStatus.SUCCEEDED : XPaymentAdapterStatus.CANCELED;
            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(finalStatus);
            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
        }, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
