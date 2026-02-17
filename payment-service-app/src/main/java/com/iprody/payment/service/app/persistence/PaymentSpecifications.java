package com.iprody.payment.service.app.persistence;

import org.springframework.data.jpa.domain.Specification;
import com.iprody.payment.service.app.persistence.entity.Payment;
import java.math.BigDecimal;
import java.time.Instant;

public final class PaymentSpecifications {

    public static Specification<Payment> hasCurrency(String currency) {
        return (root, query, cb) -> cb.equal(root.get("currency"), currency);
    }

    public static Specification<Payment> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> cb.between(root.get("amount"), min, max);
    }


    public static Specification<Payment> amountGreaterOrEqual(BigDecimal min) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("amount"), min);
    }

    public static Specification<Payment> amountLessOrEqual(BigDecimal max) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("amount"), max);
    }

    public static Specification<Payment> createdBetween(Instant after, Instant before) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), after, before);
    }

    public static Specification<Payment> createdLessOrEqual(Instant before) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), before);
    }

    public static Specification<Payment> createdGreaterOrEqual(Instant after) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), after);
    }

    public static Specification<Payment> hasStatus(String status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
