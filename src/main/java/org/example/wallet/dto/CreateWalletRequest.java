package org.example.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateWalletRequest {
    @Positive(message = "Баланс должен быть положительным")
    @NotNull(message ="Баланс не может принимать значение null")
    private double initialBalance;

}
