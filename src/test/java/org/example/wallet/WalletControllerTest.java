package org.example.wallet;

import org.example.wallet.controllers.WalletController;
import org.example.wallet.domain.OperationType;
import org.example.wallet.domain.Wallet;
import org.example.wallet.dto.CreateWalletRequest;
import org.example.wallet.dto.WalletRequest;
import org.example.wallet.exceptions.InsufficientFundsInWallet;
import org.example.wallet.exceptions.InvalidUUIDException;
import org.example.wallet.exceptions.WalletAlreadyExistsException;
import org.example.wallet.exceptions.WalletNotFoundException;
import org.example.wallet.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WalletControllerTest {
    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateWallet_Success() throws WalletAlreadyExistsException {
        CreateWalletRequest request = new CreateWalletRequest();
        request.setInitialBalance(100.0);
        Wallet mockWallet = new Wallet();
        mockWallet.setWalletId(UUID.randomUUID());
        mockWallet.setBalance(100.0);
        when(walletService.createNewWallet(request)).thenReturn(mockWallet);

        ResponseEntity<Wallet> response = walletController.createWallet(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockWallet, response.getBody());
        verify(walletService, times(1)).createNewWallet(request);
    }

    @Test
    public void testUpdateWallet_Deposit_Success() {
        WalletRequest request = new WalletRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(50.0);
        CompletableFuture<String> future = CompletableFuture.completedFuture(null);

        when(walletService.updateBalance(any(UUID.class), eq(OperationType.DEPOSIT), any(Double.class)))
                .thenReturn(future);

        ResponseEntity<String> response = walletController.updateBalance(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(walletService, times(1)).updateBalance(request.getWalletId(), OperationType.DEPOSIT, request.getAmount());
    }

    @Test
    public void testUpdateWallet_Withdraw_Success() {
        WalletRequest request = new WalletRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(50.0);
        CompletableFuture<String> future = CompletableFuture.completedFuture(null);

        when(walletService.updateBalance(any(UUID.class), any(OperationType.class), any(Double.class)))
                .thenReturn(future);

        ResponseEntity<String> response = walletController.updateBalance(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(walletService, times(1)).updateBalance(request.getWalletId(), OperationType.WITHDRAW, request.getAmount());
    }

    @Test
    public void testUpdateWallet_WalletNotFound() {
        WalletRequest request = new WalletRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(50.0);
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new WalletNotFoundException());

        when(walletService.updateBalance(any(UUID.class), any(OperationType.class), any(Double.class)))
                .thenReturn(future);

        ResponseEntity<String> response = walletController.updateBalance(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Кошелек не найден", response.getBody());
        verify(walletService, times(1)).updateBalance(request.getWalletId(), OperationType.WITHDRAW, request.getAmount());
    }

    @Test
    public void testUpdateWallet_InsufficientFunds() {
        WalletRequest request = new WalletRequest();
        request.setWalletId(UUID.randomUUID());
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(50.0);
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new InsufficientFundsInWallet(request.getWalletId()));

        when(walletService.updateBalance(any(UUID.class), any(OperationType.class), any(Double.class)))
                .thenReturn(future);

        ResponseEntity<String> response = walletController.updateBalance(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Недостаточно средств в кошельке : " + request.getWalletId(), response.getBody());
        verify(walletService, times(1)).updateBalance(request.getWalletId(), OperationType.WITHDRAW, request.getAmount());
    }

    @Test
    public void testGetBalance_Success() throws WalletNotFoundException, InvalidUUIDException {
        UUID walletId = UUID.randomUUID();
        double balance = 100.0;
        when(walletService.getBalance(walletId)).thenReturn(balance);

        ResponseEntity<Double> response = walletController.getBalance(walletId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balance, response.getBody());
        verify(walletService, times(1)).getBalance(walletId);
    }

    @Test
    public void testGetBalance_WalletNotFound() throws WalletNotFoundException, InvalidUUIDException {
        UUID walletId = UUID.randomUUID();
        when(walletService.getBalance(walletId)).thenThrow(new WalletNotFoundException());

        ResponseEntity<Double> response = walletController.getBalance(walletId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(walletService, times(1)).getBalance(walletId);
    }

    @Test
    public void testGetBalance_InvalidUUID() throws WalletNotFoundException, InvalidUUIDException {
        UUID walletId = UUID.randomUUID();
        when(walletService.getBalance(walletId)).thenThrow(new InvalidUUIDException());

        ResponseEntity<Double> response = walletController.getBalance(walletId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(walletService, times(1)).getBalance(walletId);
    }
}