package org.example.wallet.services;

import lombok.RequiredArgsConstructor;
import org.example.wallet.domain.OperationType;
import org.example.wallet.dto.CreateWalletRequest;
import org.example.wallet.exceptions.InsufficientFundsInWallet;
import org.example.wallet.exceptions.NotFoundOperationTypeException;
import org.example.wallet.exceptions.WalletNotFoundException;
import org.example.wallet.domain.Wallet;
import org.example.wallet.repositories.WalletRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionTemplate transactionTemplate;

    @Async
    public CompletableFuture<String> updateBalance(UUID uuid, OperationType operationType, double amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return transactionTemplate.execute(status -> {
                    Wallet wallet = walletRepository.findByWalletId(uuid)
                            .orElseThrow(WalletNotFoundException::new);

                    switch (operationType) {
                        case DEPOSIT:
                            wallet.setBalance(wallet.getBalance() + amount);
                            break;
                        case WITHDRAW:
                            if (wallet.getBalance() < amount) {
                                throw new InsufficientFundsInWallet(uuid);
                            }
                            wallet.setBalance(wallet.getBalance() - amount);
                            break;
                        default:
                            throw new NotFoundOperationTypeException();
                    }

                    walletRepository.save(wallet);
                    return "Баланс кошелька обновлен";
                });
            } catch (InsufficientFundsInWallet |
                     WalletNotFoundException |
                     NotFoundOperationTypeException e) {
                throw new CompletionException(e);
            }
        });
    }

    @Transactional
    public double getBalance(UUID uuid) {
        Wallet wallet = walletRepository.findByWalletId(uuid)
                .orElseThrow(WalletNotFoundException::new);
        return wallet.getBalance();
    }

    public Wallet createNewWallet(CreateWalletRequest request) {
        Wallet wallet = new Wallet();
        wallet.setWalletId(UUID.randomUUID());
        wallet.setBalance(request.getInitialBalance());
        return walletRepository.save(wallet);
    }

}
