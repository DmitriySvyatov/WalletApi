package org.example.wallet.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.wallet.domain.Wallet;
import org.example.wallet.dto.CreateWalletRequest;
import org.example.wallet.dto.WalletRequest;
import org.example.wallet.exceptions.*;
import org.example.wallet.services.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    /**
     *
     * Запрос на создание нового кошелька
     *
     * @param request принимает данные в формате
     *                {
     *                "initialBalance": Double
     *                }
     * @return Новый созданный кошелек с уникальным UUID
     * в случае если кошелек уже существует, выбрасывается исключение WalletAlreadyExistsException
     */
    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        log.info("Вызов createWallet");
        try {
            Wallet wallet = walletService.createNewWallet(request);
            log.info(MessageFormat.format("Кошелек с ID: {0} успешно создан , баланс кошелька {1}"
                    , wallet.getWalletId(),wallet.getBalance()));
            return new ResponseEntity<>(wallet, HttpStatus.CREATED);
        }catch (WalletAlreadyExistsException exception){
            log.error("Ошибка при создании кошелька"+exception.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    /**
     *
     * Запрос для обновления баланса кошелька в зависимости от типа передаваемой операции
     *
     * @param request принимает данные в формате
     *                {
     *                "walletId": "UUID",
     *                "operationType": "DEPOSIT or WITHDRAW",
     *                "amount": double
     *                }
     * @return Результат обновления баланса, в случае успешного изменения Баланс кошелька обновлен
     * в случае если кошелек не найден выбрасывается исключение WalletNotFoundException
     * в случае если баланс не достаточен для изменения выбрасывается исключение InsufficientFundsInWallet
     */
    @PostMapping()
    public ResponseEntity<String> updateBalance(@RequestBody WalletRequest request) {
        CompletableFuture<String> future = walletService.updateBalance(
                request.getWalletId(), request.getOperationType(), request.getAmount()
        );

        try {
            String result = future.join();
            return ResponseEntity.ok(result);
        } catch (CompletionException e) {
            if (e.getCause() instanceof InsufficientFundsInWallet) {
                return ResponseEntity.badRequest().body(e.getCause().getMessage());
            } else if (e.getCause() instanceof WalletNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getCause().getMessage());
            } else if (e.getCause() instanceof NotFoundOperationTypeException) {
                return ResponseEntity.badRequest().body(e.getCause().getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера");
            }
        }
    }

    /**
     *
     * Запрос на получение информации о балансе кошелька
     *
     * @param walletId принимает UUID кошелька в формате
     *                 /api/v1/wallets/{walletId}/balance
     * @return баланс выбранного кошелька,
     * в случае если кошелек не найден выбрасывается исключение WalletNotFoundException
     * в случае если неверный формат UUID выбрасывается исключение InvalidUUIDException
     */
    @GetMapping("/{walletId}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable UUID walletId) {
        log.info("Вызов getBalance у кошелька с walletID : {}", walletId);
        try {
            double balance = walletService.getBalance(walletId);
            log.info("Баланс успешно получен у кошелька walletID: {}", walletId);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (WalletNotFoundException exception) {
            log.error("Кошелек с walletId : {} не найден", walletId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (InvalidUUIDException exception) {
            log.error("Переданный walletId : {} имеет неверный формат", walletId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
