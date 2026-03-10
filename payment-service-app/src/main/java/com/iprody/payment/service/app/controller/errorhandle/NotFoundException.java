package com.iprody.payment.service.app.controller.errorhandle;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    private final UUID id;
    private final operationType operation;

    public enum operationType {
        CREATE_OP,
        DELETE_OP,
        UPDATE_OP,
        FIND_BY_ID_OP,
        FIND_ALL_OP,
        GET_OP
    }

    public NotFoundException(UUID id, operationType operation, String message) {
        super(message);
        this.id = id;
        this.operation = operation;
    }

    public UUID getId() {
        return id;
    }

    public operationType getOperation() {
        return operation;
    }
}