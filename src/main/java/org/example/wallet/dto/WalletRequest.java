package org.example.wallet.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.example.wallet.domain.OperationType;

import java.util.UUID;

@Data
public class WalletRequest {

    @NotNull(message = "Wallet ID не может быть пустым")
    private UUID walletId;

    @NotNull(message = "Operation Type не может принимать значение null")
    private OperationType operationType;

    @Positive(message = "Баланс должен быть положительным")
    private double amount;
}