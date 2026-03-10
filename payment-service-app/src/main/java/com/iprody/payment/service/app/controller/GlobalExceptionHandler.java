package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.controller.errorhandle.ErrorMessageDto;
import com.iprody.payment.service.app.controller.errorhandle.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessageDto handleNotFound(NotFoundException ex) {
        return new ErrorMessageDto(
                ex.getId(),
                ex.getOperation(),
                ex.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)

    public ErrorMessageDto handleOther(Exception ex) {
        return new ErrorMessageDto(
                null,
                null,
                ex.getMessage()
        );
    }
}
