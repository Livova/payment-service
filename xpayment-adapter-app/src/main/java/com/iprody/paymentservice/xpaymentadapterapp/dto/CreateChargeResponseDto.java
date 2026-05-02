package com.iprody.paymentservice.xpaymentadapterapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateChargeResponseDto {
    private UUID id;

    private BigDecimal amount;

    private String currency;

    private BigDecimal amountReceived;

    private String createdAt;

    private String chargedAt;

    private String customer;

    private UUID order;

    private String receiptEmail;

    private String status;
}
