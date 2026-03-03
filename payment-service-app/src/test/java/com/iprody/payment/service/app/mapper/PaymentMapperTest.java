package com.iprody.payment.service.app.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.dto.PaymentDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void shouldMapToDto() {
        // given
        UUID guid = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setGuid(guid);
        payment.setAmount(new BigDecimal("123.45"));
        payment.setCurrency("USD");
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());

        // when
        PaymentDto dto = this.paymentMapper.toDto(payment);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getGuid()).isEqualTo(payment.getGuid());
        assertThat(dto.getAmount()).isEqualTo(payment.getAmount());
        assertThat(dto.getCurrency()).isEqualTo(payment.getCurrency());
        assertThat(dto.getInquiryRefId()).isEqualTo(payment.getInquiryRefId());
        assertThat(dto.getStatus()).isEqualTo(payment.getStatus());
        assertThat(dto.getCreatedAt()).isEqualTo(payment.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(payment.getUpdatedAt());
    }
}
