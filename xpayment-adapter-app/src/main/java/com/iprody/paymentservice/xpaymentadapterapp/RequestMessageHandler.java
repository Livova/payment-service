package com.iprody.paymentservice.xpaymentadapterapp;

import com.iprody.paymentservice.xpaymentadapterapp.api.XPaymentProviderGateway;
import com.iprody.paymentservice.xpaymentadapterapp.async.*;
import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.iprody.paymentservice.xpaymentadapterapp.async.*;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;
import com.iprody.paymentservice.xpaymentadapterapp.checkstate.PaymentStateCheckRegistrar;

@Slf4j
@AllArgsConstructor
@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final PaymentStateCheckRegistrar paymentStateCheckRegistrar;

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

        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
                message.getPaymentGuid(), message.getAmount(), message.getCurrency());

        CreateChargeRequestDto createChargeRequestDto = new CreateChargeRequestDto();
        createChargeRequestDto.setAmount(message.getAmount());
        createChargeRequestDto.setCurrency(message.getCurrency());
        createChargeRequestDto.setOrder(message.getPaymentGuid());

        try {
            CreateChargeResponseDto chargeResponseDto = xPaymentProviderGateway.createCharge(createChargeRequestDto);
            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                    chargeResponseDto.getStatus());

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(chargeResponseDto.getOrder());
            responseMessage.setTransactionRefId(chargeResponseDto.getId());
            responseMessage.setAmount(chargeResponseDto.getAmount());
            responseMessage.setCurrency(chargeResponseDto.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponseDto.getStatus()));
            responseMessage.setOccurredAt(OffsetDateTime.now());

            sender.send(responseMessage);
            paymentStateCheckRegistrar.register(
                    chargeResponseDto.getId(),
                    chargeResponseDto.getOrder(),
                    chargeResponseDto.getAmount(),
                    chargeResponseDto.getCurrency()
            );
        } catch (RestClientException ex) {
            log.error("Error in time of sending payment request with paymentGuid - {}", message.getPaymentGuid(), ex);

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            responseMessage.setOccurredAt(OffsetDateTime.now());

            sender.send(responseMessage);
        }
    }
}
