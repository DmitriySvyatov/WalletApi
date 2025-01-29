package org.example.wallet.services;

import lombok.RequiredArgsConstructor;
import org.example.wallet.domain.OperationType;
import org.example.wallet.dto.CreateWalletRequest;
import org.example.wallet.exeption.InsufficientFundsInWallet;
import org.example.wallet.exeption.NotFoundOperationTypeException;
import org.example.wallet.exeption.WalletNotFoundException;
import org.example.wallet.domain.Wallet;
import org.example.wallet.repositoryies.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public void updateBalance(UUID uuid, OperationType operationType, double amount) {
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
    }

    @Transactional(readOnly = true)
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
