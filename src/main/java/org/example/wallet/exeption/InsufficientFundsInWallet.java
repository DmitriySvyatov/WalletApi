package org.example.wallet.exeption;

import java.util.UUID;

public class InsufficientFundsInWallet extends RuntimeException {
    public InsufficientFundsInWallet(UUID uuid) {
        super("Недостаточно средств в кошельке : " + uuid);
    }
}
