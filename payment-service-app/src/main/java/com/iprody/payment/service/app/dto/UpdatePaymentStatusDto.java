package com.iprody.payment.service.app.dto;

import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdatePaymentStatusDto {
    private PaymentStatus status;
}
