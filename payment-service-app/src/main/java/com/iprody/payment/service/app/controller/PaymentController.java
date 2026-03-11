package com.iprody.payment.service.app.controller;
import com.iprody.payment.service.app.dto.CreatePaymentDto;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.UpdatePaymentNoteDto;
import com.iprody.payment.service.app.dto.UpdatePaymentStatusDto;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<PaymentDto> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/search")
    public Page<PaymentDto> search(
        @ModelAttribute PaymentFilter filter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "25") int size,
        @RequestParam(defaultValue = "guid") String sortBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {
        final Sort sort = switch (direction) {
            case "asc" -> Sort.by(sortBy).ascending();
            case "desc" -> Sort.by(sortBy).descending();
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        };

        final Pageable pageRequest = PageRequest.of(page, size, sort);
        return paymentService.search(filter, pageRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto get(@PathVariable UUID id) {
        return paymentService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(@RequestBody CreatePaymentDto paymentDto) {
        return paymentService.create(paymentDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete (@PathVariable UUID id) {
        paymentService.delete(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public void updateStatus(@PathVariable("id") UUID id, @RequestBody UpdatePaymentStatusDto dto) {
        paymentService.updateStatus(id, dto.getStatus());
    }

    @PatchMapping("/{id}/note")
    @ResponseStatus(HttpStatus.OK)
    public void updateNote(@PathVariable("id") UUID id, @RequestBody UpdatePaymentNoteDto dto) {
        paymentService.updateNote(id, dto.getNote());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto update(@PathVariable("id") UUID id, @RequestBody PaymentDto paymentDto) {
        return paymentService.update(id, paymentDto);
    }
}