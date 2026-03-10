package com.iprody.payment.service.app.controller.errorhandle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorMessageDto {
    private UUID id;
    @NonNull private final NotFoundException.operationType operation;
    @NonNull private final String errorMessage;
    private final Instant timestamp = Instant.now();
}