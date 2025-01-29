package org.example.wallet.exeption;

public class WalletAlreadyExistsException extends RuntimeException {
    public WalletAlreadyExistsException() {
        super("Кошелек уже существует");
    }
}
