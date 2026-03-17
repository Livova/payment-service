package com.iprody.payment.service.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;

@SpringBootTest
class PaymentServiceAppApplicationTests {
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private Clock clock;
    @Test
    void contextLoads() {
    }

}
