package org.example.wallet.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Entity
@Data
@Table(name = "wallets",indexes =
        {@Index(name = "idx_wallet_id",columnList = "walletId")})
public class Wallet {

    /**
     * Идентификатор кошелька
     */
    @Id
    private UUID walletId;

    /**
     * Баланс кошелька
     */
    private Double balance;


}
