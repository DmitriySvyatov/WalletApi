package org.example.wallet.exeption;


public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException() {
        super("Кошелек не найден");
    }
}
