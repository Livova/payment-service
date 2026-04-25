package com.iprody.paymentservice.xpaymentadapterapp.kafka;

import com.iprody.paymentservice.xpaymentadapterapp.BusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, e) -> new TopicPartition(
                        record.topic() + ".DLT",
                        record.partition()
                )
        );
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0L));

        handler.addNotRetryableExceptions(BusinessException.class);

        return handler;
    }
}