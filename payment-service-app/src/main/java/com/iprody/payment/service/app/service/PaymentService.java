package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public List<PaymentDto> findAll() {
        return this.paymentRepository.findAll().stream()
            .map(paymentMapper::toDto)
            .toList();
    }

    public Optional<PaymentDto> findById(UUID guid) {
        return this.paymentRepository.findById(guid)
                .map(paymentMapper::toDto);
    }

    public Page<PaymentDto> search(PaymentFilter filter, Pageable pageable) {
        return this.paymentRepository.findAll(PaymentFilterFactory.fromFilter(filter), pageable)
           .map(paymentMapper::toDto);
    }

    public PaymentDto create(CreatePaymentDto paymentDto) {
        return this.paymentMapper.toDto(
                this.paymentRepository.save(paymentMapper.fromCreateDto(paymentDto))
        );
    }
}
