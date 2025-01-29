package org.example.wallet.exeption;

public class NotFoundOperationTypeException extends RuntimeException {
    public NotFoundOperationTypeException() {
        super("Неизвестная операция");
    }
}
