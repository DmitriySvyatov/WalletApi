package org.example.wallet.exceptions;


public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException() {
        super("Кошелек не найден");
    }
}
