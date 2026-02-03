package com.iprody.payment.service.app.controller;
import com.iprody.payment.service.app.persistence.PaymentRepository;
import com.iprody.payment.service.app.persistence.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentRepository paymentRepository;
    @GetMapping
    public List<Payment> getPayment() {
        return paymentRepository.findAll();
    }
    @GetMapping("/{guid}")
    public Payment getPayment(@PathVariable UUID guid) {
        return paymentRepository.findById(guid).orElseThrow(() -> new RuntimeException("Payment Not found"));
        //System.out.println("string " + payments.toString());
        //return payments.get(id);
    }
}