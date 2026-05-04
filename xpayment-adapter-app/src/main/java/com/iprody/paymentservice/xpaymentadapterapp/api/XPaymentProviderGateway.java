package com.iprody.paymentservice.xpaymentadapterapp.api;

import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeResponseDto;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

public interface XPaymentProviderGateway {

    CreateChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequest) throws RestClientException;

    CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException;

}
