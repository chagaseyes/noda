package com.noda.api.services;

import com.noda.api.exceptions.AccountNotFoundException;
import com.noda.api.exceptions.InsufficientFoundsException;
import com.noda.api.exceptions.SameAccountTransferException;
import com.noda.api.models.Account;
import com.noda.api.repositories.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }


  @Transactional
  public void transfer(Long sourceId, Long targetId, BigDecimal amount) {
        if(sourceId.equals(targetId)) {
            throw new SameAccountTransferException("Source and target IDs are the same: " + sourceId);
        }

      Account source = accountRepository.findById(sourceId)
              .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
      Account target = accountRepository.findById(targetId)
              .orElseThrow(() -> new AccountNotFoundException("Target account not found"));

      if (source.getBalance().compareTo(amount) < 0) {
          throw new InsufficientFoundsException("Insufficient funds in account ");
      }

      source.setBalance(source.getBalance().subtract(amount));
      target.setBalance(target.getBalance().add(amount));

      accountRepository.save(source);
      accountRepository.save(target);
  }
}
