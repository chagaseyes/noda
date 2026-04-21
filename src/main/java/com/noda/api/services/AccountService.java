package com.noda.api.services;

import com.noda.api.exceptions.AccountNotFoundException;
import com.noda.api.exceptions.InsufficientFundsException;
import com.noda.api.exceptions.SameAccountTransferException;
import com.noda.api.models.Account;
import com.noda.api.models.User;
import com.noda.api.models.enums.AccountType;
import com.noda.api.repositories.AccountRepository;
import com.noda.api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account createAccount(Long userId, String accountNumber, BigDecimal balance, AccountType type) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account newAccount = new Account();
        newAccount.setAccountNumber(accountNumber);
        newAccount.setAccountType(type);
        newAccount.setBalance(balance);
        newAccount.setUser(owner);

        return accountRepository.save(newAccount);
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

  @Transactional
  public void transfer(Long sourceId, Long targetId, BigDecimal amount) {
       if(amount.compareTo(BigDecimal.ZERO) <= 0) {
           throw new IllegalArgumentException("Transfer amount must be positive");
       }

        if(sourceId.equals(targetId)) {
            throw new SameAccountTransferException("Source and target IDs are the same: " + sourceId);
        }

      Account source = accountRepository.findById(sourceId)
              .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
      Account target = accountRepository.findById(targetId)
              .orElseThrow(() -> new AccountNotFoundException("Target account not found"));

      if (source.getBalance().compareTo(amount) < 0) {
          throw new InsufficientFundsException("Insufficient funds in account: " +source.getAccountNumber());
      }

      source.setBalance(source.getBalance().subtract(amount));
      target.setBalance(target.getBalance().add(amount));
  }

  @Transactional
  public void deposit(Long id, BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <=0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
  }
}
