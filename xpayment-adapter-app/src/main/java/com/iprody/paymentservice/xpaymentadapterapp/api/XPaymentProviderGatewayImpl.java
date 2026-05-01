package com.iprody.paymentservice.xpaymentadapterapp.api;

import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import com.iprody.xpayment.api.client.DefaultApi;
import com.iprody.paymentservice.xpaymentadapterapp.mapper.XPaymentProviderMapper;
import com.iprody.xpayment.api.model.ChargeResponse;
import com.iprody.xpayment.api.model.CreateChargeRequest;

import java.util.UUID;

@Service
@AllArgsConstructor
public class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {

    private final DefaultApi defaultApi;

    private final XPaymentProviderMapper xPaymentProviderMapper;

    @Override
    public CreateChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequestDto) throws RestClientException {
        final CreateChargeRequest createChargeRequest = xPaymentProviderMapper.toCreateChargeRequest(createChargeRequestDto);
        final ChargeResponse createChargeResponse = defaultApi.createCharge(createChargeRequest);
        return xPaymentProviderMapper.toCreateChargeResponseDto(createChargeResponse);
    }

    @Override
    public CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException {
        final ChargeResponse createChargeResponse = defaultApi.retrieveCharge(id);
        return xPaymentProviderMapper.toCreateChargeResponseDto(createChargeResponse);
    }
}
