package org.example.wallet.exceptions;

import java.util.UUID;

public class InsufficientFundsInWallet extends RuntimeException {
    public InsufficientFundsInWallet(UUID uuid) {
        super("Недостаточно средств в кошельке : " + uuid);
    }
}
