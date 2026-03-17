package com.iprody.payment.service.app.controller.errorhandle;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorMessageDtoResponse {
    private UUID id;
    private NotFoundException.operationType operation;
    private String errorMessage;
    private Instant timestamp;
}