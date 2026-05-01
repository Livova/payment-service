package com.iprody.paymentservice.xpaymentadapterapp.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCheckStateMessage(
        UUID chargeGuid,
        UUID paymentGuid,
        BigDecimal amount,
        String currency
        ) { }
