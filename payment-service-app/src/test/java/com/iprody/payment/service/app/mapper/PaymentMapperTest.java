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
        payment.setCreatedAt(OffsetDateTime.parse("2026-01-01T00:00:00+01:00"));
        payment.setUpdatedAt(OffsetDateTime.parse("2026-01-01T00:00:00+01:00"));

        // when
        PaymentDto dto = this.paymentMapper.toDto(payment);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto)
                .returns(payment.getGuid(), PaymentDto::getGuid)
                .returns(payment.getAmount(), PaymentDto::getAmount)
                .returns(payment.getCurrency(), PaymentDto::getCurrency)
                .returns(payment.getInquiryRefId(), PaymentDto::getInquiryRefId)
                .returns(payment.getStatus(), PaymentDto::getStatus)
                .returns(payment.getCreatedAt(), PaymentDto::getCreatedAt)
                .returns(payment.getUpdatedAt(), PaymentDto::getUpdatedAt);

    }

    @Test
    void shouldMapToEntity() {
        // given
        UUID guid = UUID.randomUUID();
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setGuid(guid);
        paymentDto.setAmount(new BigDecimal("123.45"));
        paymentDto.setCurrency("USD");
        paymentDto.setInquiryRefId(UUID.randomUUID());
        paymentDto.setStatus(PaymentStatus.APPROVED);
        paymentDto.setCreatedAt(OffsetDateTime.parse("2026-01-01T00:00:00+01:00"));
        paymentDto.setUpdatedAt(OffsetDateTime.parse("2026-01-01T00:00:00+01:00"));

        // when
        Payment entity = this.paymentMapper.toEntity(paymentDto);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getGuid()).isEqualTo(paymentDto.getGuid());
        assertThat(entity.getAmount()).isEqualTo(paymentDto.getAmount());
        assertThat(entity.getCurrency()).isEqualTo(paymentDto.getCurrency());
        assertThat(entity.getInquiryRefId()).isEqualTo(paymentDto.getInquiryRefId());
        assertThat(entity.getStatus()).isEqualTo(paymentDto.getStatus());
        assertThat(entity.getCreatedAt()).isEqualTo(paymentDto.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(paymentDto.getUpdatedAt());
    }
}
