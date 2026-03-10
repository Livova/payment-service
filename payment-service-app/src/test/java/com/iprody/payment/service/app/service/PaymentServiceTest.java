package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.dto.PaymentDto;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private UUID guid;
    private PaymentFilter filter;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        guid = UUID.randomUUID();

        payment = new Payment();
        payment.setGuid(guid);
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(OffsetDateTime.parse("2026-01-01T00:00:00+01:00"));
        payment.setUpdatedAt(OffsetDateTime.parse("2026-01-01T00:00:00+01:00"));

        paymentDto = new PaymentDto();
        paymentDto.setGuid(payment.getGuid());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setStatus(payment.getStatus());
        pageable = PageRequest.of(0, 25, Sort.by("amount").ascending());
    }

    @Test
    void shouldReturnPaymentById() {
        // given
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // when
        Optional<PaymentDto> resultOpt = paymentService.findById(guid);

        // then
        assertTrue(resultOpt.isPresent());
        PaymentDto result = resultOpt.get();
        assertEquals(guid, result.getGuid());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldGetById() {
        // given
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto result = paymentService.get(guid);

        // then
        assertEquals(guid, result.getGuid());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldCreate() {
        // given
        CreatePaymentDto createPaymentDto = new CreatePaymentDto(paymentDto.getGuid(),
                paymentDto.getAmount(),
                paymentDto.getCurrency(),
                paymentDto.getTransactionRefId(),
                paymentDto.getStatus(),
                paymentDto.getNote());
        when(paymentMapper.fromCreateDto(createPaymentDto)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto result = paymentService.create(createPaymentDto);

        // then
        assertEquals(guid, result.getGuid());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(paymentMapper).fromCreateDto(createPaymentDto);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldUpdate() {
        // given
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toEntity(paymentDto)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        when(paymentRepository.existsById(guid)).thenReturn(Boolean.TRUE);

        // when
        PaymentDto result = paymentService.update(guid, paymentDto);

        // then
        assertEquals(guid, result.getGuid());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldUpdateStatus() {
        // given

        // when
        paymentService.updateStatus(guid, paymentDto.getStatus());

        // then
        verify(paymentRepository).updateStatus(guid, paymentDto.getStatus());
    }

    @Test
    void shouldUpdateNote() {
        // given

        // when
        paymentService.updateNote(guid, paymentDto.getNote());

        // then
        verify(paymentRepository).updateNote(guid, paymentDto.getNote());
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void shouldMapDifferentPaymentStatuses(PaymentStatus status) {
        // given
        payment.setStatus(status);
        paymentDto.setStatus(status);

        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // when
        Optional<PaymentDto> resultOpt = paymentService.findById(guid);

        // then
        assertTrue(resultOpt.isPresent());
        PaymentDto result = resultOpt.get();
        assertEquals(status, result.getStatus());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    static PaymentStatus[] statusProvider() {
        return PaymentStatus.values();
    }

    @ParameterizedTest
    @MethodSource("filtersProvider")
    void shouldFindByAllParams(PaymentFilter filter) {
        // given
        ArrayList<Payment> paymentList = new ArrayList<Payment>();
        paymentList.add(payment);
        //имитируем работу фильтрации
        List<Payment> paymentFiltred = paymentList.stream()
                .filter(n -> (filter.currency()==null ? n.getCurrency() : filter.currency()).equals(n.getCurrency()))
                .filter(n -> (filter.status()==null ? n.getStatus() : filter.status()).equals(n.getStatus()))
                .filter(n -> (filter.minAmount()==null ? n.getAmount() : filter.minAmount()).compareTo(n.getAmount()) <= 0)
                .filter(n -> (filter.maxAmount()==null ? n.getAmount() : filter.maxAmount()).compareTo(n.getAmount()) >= 0)
                .filter(n -> (filter.createdAfter()==null ? n.getCreatedAt().toInstant() : filter.createdAfter()).compareTo(n.getCreatedAt().toInstant()) <= 0)
                .filter(n -> (filter.createdBefore()==null ? n.getCreatedAt().toInstant() : filter.createdBefore()).compareTo(n.getCreatedAt().toInstant()) >= 0)
                .toList();
        when(paymentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(paymentFiltred));
        if (!paymentFiltred.isEmpty()) {
            PaymentDto paymentDtoFilter = new PaymentDto();
            paymentDtoFilter.setGuid(paymentFiltred.getFirst().getGuid());
            paymentDtoFilter.setCurrency(paymentFiltred.getFirst().getCurrency());
            paymentDtoFilter.setAmount(paymentFiltred.getFirst().getAmount());
            paymentDtoFilter.setStatus(paymentFiltred.getFirst().getStatus());
            when(paymentMapper.toDto(payment)).thenReturn(paymentDtoFilter);
        }

        // when
        Page<PaymentDto> resultPage = paymentService.search(filter, pageable);

        // then
        for (PaymentDto result : resultPage) {
            assertEquals("USD", result.getCurrency());
            assertEquals(PaymentStatus.APPROVED, result.getStatus());
            assertEquals(new BigDecimal("100.00"), result.getAmount());
        }

        verify(paymentRepository).findAll(any(Specification.class), any(Pageable.class));
        if (!paymentFiltred.isEmpty()) {
            verify(paymentMapper).toDto(payment);
        }
    }

    @Test
    void shouldFindWithPaginationAndSort() {
        // given
        filter = new PaymentFilter(null, null, null, null, null, null);
        ArrayList<Payment> paymentList = new ArrayList<Payment>();
        for (int i=0; i<25; i++) {
            paymentList.add(payment);
        }
        when(paymentRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(paymentList));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // when
        Page<PaymentDto> resultPage = paymentService.search(filter, pageable);

        // then
        List<PaymentDto> results = resultPage.getContent();
        assertEquals(25, results.size());
        verify(paymentRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(paymentMapper,atLeastOnce()).toDto(payment);
    }

    static List<PaymentFilter> filtersProvider() {
        List<PaymentFilter> filters = new ArrayList<>();

        // 1. Только валюта
        filters.add(new PaymentFilter("USD", null, null, null, null, null));

        // 2. Только статус
        filters.add(new PaymentFilter(null, null, null, null, null, "APPROVED"));

        // 3. Минимальная сумма
        filters.add(new PaymentFilter(null, new BigDecimal("10.00"), null, null, null, null));

        // 4. Максимальная сумма
        filters.add(new PaymentFilter(null, null, new BigDecimal("100.00"), null, null, null));

        // 5. Диапазон сумм
        filters.add(new PaymentFilter(null, new BigDecimal("50.00"), new BigDecimal("200.00"), null, null, null));

        // 6. Создано после
        filters.add(new PaymentFilter(null, null, null, Instant.parse("2026-01-01T00:00:00Z"), null, null));

        // 7. Создано до
        filters.add(new PaymentFilter(null, null, null, null, Instant.parse("2026-01-30T00:00:00Z"), null));

        // 8. Диапазон дат создания
        filters.add(new PaymentFilter(null, null, null,
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-30T00:00:00Z"),
                null));

        // 9. Валюта + статус
        filters.add(new PaymentFilter("USD", null, null, null, null, "NEW"));

        // 10. Валюта + диапазон сумм
        filters.add(new PaymentFilter("EUR", new BigDecimal("20.00"), new BigDecimal("80.00"), null, null, null));

        // 11. Статус + диапазон сумм
        filters.add(new PaymentFilter(null, new BigDecimal("100.00"), new BigDecimal("500.00"), null, null, "FAILED"));

        // 12. Валюта + createdAfter
        filters.add(new PaymentFilter("GBP", null, null, Instant.parse("2026-01-01T00:00:00Z"), null, null));

        // 13. Валюта + createdBefore
        filters.add(new PaymentFilter("USD", null, null, null, Instant.parse("2026-01-30T00:00:00Z"), null));

        // 14. Все
        filters.add(new PaymentFilter(
                "EUR",
                new BigDecimal("25.00"),
                new BigDecimal("250.00"),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-31T00:00:00Z"),
                "APPROVED"
        ));

        // 15. Пустой фильтр (ничего не задано)
        filters.add(new PaymentFilter(null, null, null, null, null, null));

        return filters;
    }
}
