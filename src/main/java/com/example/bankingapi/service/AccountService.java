package com.example.bankingapi.service;

import com.example.bankingapi.model.Account;
import com.example.bankingapi.repository.AccountRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    public Account getAccountBalance(Long accountId) {
        log.info("Fetching balance for account ID: {}", accountId);
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Transactional
    @RateLimiter(name = "withdrawRateLimiter")
    public void withdraw(Long accountId, BigDecimal amount) {
        log.info("Attempting to withdraw {} from account ID: {}", amount, accountId);
        Account account = getAccountBalance(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds for withdrawal on account ID: {}", accountId);
            throw new IllegalArgumentException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        log.info("Successfully withdrew {} from account ID: {}", amount, accountId);
    }

    @Transactional
    @CircuitBreaker(name = "depositCircuitBreaker", fallbackMethod = "depositFallback")
    public void deposit(Long accountId, BigDecimal amount) {
        log.info("Attempting to deposit {} into account ID: {}", amount, accountId);
        Account account = getAccountBalance(accountId);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        log.info("Successfully deposited {} into account ID: {}", amount, accountId);
    }

    public void depositFallback(Long accountId, BigDecimal amount, Throwable t) {
        log.error("Deposit to account ID: {} failed due to a circuit breaker. Error: {}", accountId, t.getMessage());
        throw new RuntimeException("Deposit service is currently unavailable. Please try again later.");
    }
}