package org.example.wallet.exceptions;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException() {
        super("Кошелек уже существует");
    }
}
