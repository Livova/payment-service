package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.PaymentRepository;

import java.time.OffsetDateTime;
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

    public PaymentDto get(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Платеж не найден: " + id));
    }

    public Page<PaymentDto> search(PaymentFilter filter, Pageable pageable) {
        return this.paymentRepository.findAll(PaymentFilterFactory.fromFilter(filter), pageable)
           .map(paymentMapper::toDto);
    }

    public PaymentDto create(CreatePaymentDto paymentDto) {
        final Payment payment = paymentMapper.fromCreateDto(paymentDto);
        final OffsetDateTime time = OffsetDateTime.now();
        payment.setCreatedAt(time);
        payment.setUpdatedAt(time);
        payment.setGuid(UUID.randomUUID());
        return this.paymentMapper.toDto(
                this.paymentRepository.save(payment)
        );
    }

    public void delete(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Платеж не найден: " + id);
        }
        this.paymentRepository.deleteById(id);
    }

    public PaymentDto update(UUID id, PaymentDto dto) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Платеж не найден: " + id);
        }
        final Payment updated = paymentMapper.toEntity(dto);
        updated.setGuid(id);
        final Payment saved = paymentRepository.save(updated);
        return paymentMapper.toDto(saved);
    }

    @Transactional
    public void updateStatus(UUID id, PaymentStatus status) {
        this.paymentRepository.updateStatus(id, status);
    }

    @Transactional
    public void updateNote(UUID id, String note) {
        this.paymentRepository.updateNote(id, note);
    }
}
