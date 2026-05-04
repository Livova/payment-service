package com.iprody.paymentservice.xpaymentadapterapp.mapper;

import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.paymentservice.xpaymentadapterapp.dto.CreateChargeResponseDto;
import com.iprody.xpayment.api.model.ChargeResponse;
import com.iprody.xpayment.api.model.CreateChargeRequest;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface XPaymentProviderMapper {
    CreateChargeResponseDto toCreateChargeResponseDto(ChargeResponse chargeResponse);
    CreateChargeRequest toCreateChargeRequest(CreateChargeRequestDto createChargeRequestDto);
}