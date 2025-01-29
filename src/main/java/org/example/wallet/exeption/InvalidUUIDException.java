package org.example.wallet.exeption;

public class InvalidUUIDException extends RuntimeException {
    public InvalidUUIDException() {
        super("Неверный формат UUID");
    }
}
