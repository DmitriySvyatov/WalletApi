package org.example.wallet.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Wallet {

    /**
     * Идентификатор кошелька
     */
    @Id
    private UUID walletId;

    /**
     * Баланс кошелька
     */
    private double balance;


}
