package org.example.wallet.exceptions;

public class NotFoundOperationTypeException extends RuntimeException {
    public NotFoundOperationTypeException() {
        super("Неизвестная операция");
    }
}
