package com.iprody.payment.service.app.controller;
import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.UpdatePaymentNoteDto;
import com.iprody.payment.service.app.dto.UpdatePaymentStatusDto;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<PaymentDto> findAll() {
        return paymentService.findAll();
    }

    /**
     * Поиск платежей по фильтру и постранично.
     * Доступ разрешён admin и reader.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public Page<PaymentDto> search(
        @ModelAttribute PaymentFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "guid") String sortBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("Search payments with filter={}", filter);
        final Sort sort = switch (direction) {
            case "asc" -> Sort.by(sortBy).ascending();
            case "desc" -> Sort.by(sortBy).descending();
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        };

        final Pageable pageRequest = PageRequest.of(page, size, sort);
        final Page<PaymentDto> result = paymentService.search(filter, pageRequest);

        log.debug("Search payments result: page={}, size={}, totalElements={}, totalPages={}",
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages());
        return result;
    }

    /**
     * Получение одного платежа по UUID.
     * Доступ разрешён admin и reader.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto get(@PathVariable UUID id) {
        log.info("Get payment with id {}", id);
        final PaymentDto result = paymentService.get(id);
        log.debug("Get payment result: id={}, state={}", result.getGuid(), result);
        return result;
    }

    @GetMapping("/debug")
    public Object debug(Authentication auth) {
        return auth.getAuthorities();
    }

    /**
     * Создание нового платежа.
     * Доступ разрешён только пользователям с ролью admin.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto create(@RequestBody CreatePaymentDto paymentDto)  {
        log.info("Create payment with data {}", paymentDto);
        final PaymentDto result = paymentService.create(paymentDto);
        log.debug("Create payment result: id={}, state={}", result.getGuid(), result);
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void delete (@PathVariable UUID id) {
        log.info("Delete payment with id {}", id);
        paymentService.delete(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('admin')")
    public void updateStatus(@PathVariable("id") UUID id, @RequestBody UpdatePaymentStatusDto dto) {
        log.info("Update payment status with id {} and status {}", id, dto.getStatus());
        paymentService.updateStatus(id, dto.getStatus());
    }

    @PatchMapping("/{id}/note")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('admin')")
    public void updateNote(@PathVariable("id") UUID id, @RequestBody UpdatePaymentNoteDto dto) {
        log.info("Update payment status with id {} and note {}", id, dto.getNote());
        paymentService.updateNote(id, dto.getNote());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto update(@RequestBody PaymentDto paymentDto) {
        log.info("Update payment with id {} and data {}", paymentDto.getGuid(), paymentDto);
        final PaymentDto result = paymentService.update(paymentDto.getGuid(), paymentDto);
        log.debug("Update payment result: id={}, state={}", result.getGuid(), result);
        return result;
    }
}