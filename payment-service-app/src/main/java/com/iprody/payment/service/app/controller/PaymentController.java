package com.iprody.payment.service.app.controller;
import com.iprody.payment.service.app.model.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/payments")
public class PaymentController {
        private final int ThisIsTestVar;
    //private final Payment payment = new Payment(1L, 0);
    private final Map<Long, Payment> payments = new HashMap<>();
    {
        payments.put(1L, new Payment(1L, 1));
        payments.put(2L, new Payment(2L, 2));
        payments.put(3L, new Payment(3L, 3));
        payments.put(4L, new Payment(4L, 4));
    }
    @GetMapping
    public List<Payment> getPayment() {
        return new ArrayList<>(payments.values());
    }
    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        //System.out.println("size "+payments.size());
        //System.out.println("id "+id);
        System.out.println("string " + payments.toString());
        return payments.get(id);
    }
}