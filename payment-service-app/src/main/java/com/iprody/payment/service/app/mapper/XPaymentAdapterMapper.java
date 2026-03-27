package com.iprody.payment.service.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.iprody.payment.service.app.async.XPaymentAdapterRequestMessage;
import com.iprody.payment.service.app.dto.PaymentDto;

@Mapper(componentModel = "spring")
public interface XPaymentAdapterMapper {

    @Mapping(target = "paymentGuid", source = "guid")
    XPaymentAdapterRequestMessage toXPaymentAdapterRequestMessage(PaymentDto paymentDto);
}
