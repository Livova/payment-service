package com.iprody.paymentservice.xpaymentadapterapp.checkstate;

import java.util.UUID;

public interface PaymentStateCheckHandler {

    boolean handle(UUID chargeGuid);
}
