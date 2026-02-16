package com.iprody.payment.service.app.controller;
import com.iprody.payment.service.app.persistence.PaymentFilter;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.*;
import com.iprody.payment.service.app.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<Payment> findAll() {
        return paymentService.findAll();
    }

    @GetMapping("/search")
    public Page<Payment> search(
            @ModelAttribute PaymentFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "guid") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = switch (direction) {
            case "asc" -> Sort.by(sortBy).ascending();
            case "desc" -> Sort.by(sortBy).descending();
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        };

        Pageable pageRequest = PageRequest.of(page, size, sort);
        return paymentService.search(filter, pageRequest);
    }
}