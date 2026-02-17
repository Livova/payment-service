package com.iprody.payment.service.app.persistence;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import com.iprody.payment.service.app.persistence.entity.Payment;

public final class PaymentFilterFactory {

    public static Specification<Payment> fromFilter(PaymentFilter filter) {
        Specification<Payment> spec = Specification.unrestricted();

        if (StringUtils.hasText(filter.currency())) {
            spec = spec.and(PaymentSpecifications.hasCurrency(filter.currency()));
        }

        if (filter.minAmount() != null && filter.maxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountBetween(
                    filter.minAmount(), filter.maxAmount()));
        }
        else if (filter.minAmount() != null && filter.maxAmount() == null) {
            spec = spec.and(PaymentSpecifications.amountGreaterOrEqual(
                    filter.minAmount()));
        }
        else if (filter.minAmount() == null && filter.maxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountLessOrEqual(
                    filter.maxAmount()));
        }

        if (filter.createdAfter() != null && filter.createdBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBetween(
                    filter.createdAfter(), filter.createdBefore()));
        }
        else if (filter.createdAfter() != null && filter.createdBefore() == null) {
            spec = spec.and(PaymentSpecifications.createdLessOrEqual(
                    filter.createdAfter()));
        }
        else if (filter.createdAfter() == null && filter.createdBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdGreaterOrEqual(
                    filter.createdBefore()));
        }
        if (StringUtils.hasText(filter.status())) {
            spec = spec.and(PaymentSpecifications.hasStatus(filter.status()));
        }

        return spec;
    }
}
