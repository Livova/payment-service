package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.controller.errorhandle.ErrorMessageDto;
import com.iprody.payment.service.app.controller.errorhandle.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
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

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessageDto handleAccessDenied(AccessDeniedException ex) {
        final String msg = ex.getMessage() != null ? ex.getMessage() : "Отсутствуют права на выполнение операции";
        return new ErrorMessageDto(
                null,
                null,
                ex.getMessage()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessageDto handleAuthentication(AuthenticationException ex) {
        return new ErrorMessageDto(
                null,
                null,
                ex.getMessage()
        );
    }

    @ExceptionHandler(org.springframework.security.oauth2.core.OAuth2AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessageDto handleInvalidJwt(OAuth2AuthenticationException ex) {
        final String msg = ex.getError() != null ? ex.getError().getDescription() : ex.getMessage();
        return new ErrorMessageDto(
                null,
                null,
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
