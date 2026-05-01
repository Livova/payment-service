package com.iprody.paymentservice.xpaymentadapterapp.checkstate;

import com.iprody.paymentservice.xpaymentadapterapp.api.XPaymentProviderGateway;
import com.iprody.paymentservice.xpaymentadapterapp.async.AsyncSender;
import com.iprody.paymentservice.xpaymentadapterapp.async.XPaymentAdapterResponseMessage;
import com.iprody.paymentservice.xpaymentadapterapp.async.XPaymentAdapterStatus;
import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentStateCheckHandlerImpl implements PaymentStateCheckHandler {
    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;

    @Override
    public boolean handle(UUID chargeGuid) {
        log.info("Checking payment status for chargeGuid={}", chargeGuid);
        CreateChargeResponseDto chargeResponseDto = xPaymentProviderGateway.retrieveCharge(chargeGuid);
        String status = chargeResponseDto.getStatus();
        log.info("Current status for chargeGuid={} is {}", chargeGuid, status);
        if (status.equals("CANCELED") || status.equals("SUCCEEDED")) {
            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(chargeResponseDto.getOrder());
            responseMessage.setTransactionRefId(chargeResponseDto.getId());
            responseMessage.setAmount(chargeResponseDto.getAmount());
            responseMessage.setCurrency(chargeResponseDto.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponseDto.getStatus()));
            responseMessage.setOccurredAt(OffsetDateTime.now());

            asyncSender.send(responseMessage);
            return true;
        }

        return false;
    }
}
