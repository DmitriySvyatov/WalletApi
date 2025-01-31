package org.example.wallet.exceptions;

public class InvalidUUIDException extends RuntimeException {
    public InvalidUUIDException() {
        super("Неверный формат UUID");
    }
}
