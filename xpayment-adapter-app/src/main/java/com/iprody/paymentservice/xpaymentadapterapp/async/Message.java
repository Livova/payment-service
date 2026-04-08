package com.iprody.paymentservice.xpaymentadapterapp.async;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface Message {

    UUID getMessageId();

    OffsetDateTime getOccurredAt();
}
