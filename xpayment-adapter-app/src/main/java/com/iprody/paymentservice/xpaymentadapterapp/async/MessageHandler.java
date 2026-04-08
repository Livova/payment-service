package com.iprody.paymentservice.xpaymentadapterapp.async;

public interface MessageHandler<T extends Message> {

    void handle(T message);
}
