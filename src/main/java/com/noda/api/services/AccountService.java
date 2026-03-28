package com.noda.api.services;

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
      Account source = accountRepository.findById(sourceId)
              .orElseThrow(() -> new RuntimeException("Source account not found"));
      Account target = accountRepository.findById(targetId)
              .orElseThrow(() -> new RuntimeException("Target account not found"));

      if (source.getBalance().compareTo(amount) < 0) {
          throw new RuntimeException("Insufficient funds");
      }

      source.setBalance(source.getBalance().subtract(amount));
      target.setBalance(target.getBalance().add(amount));

      accountRepository.save(source);
      accountRepository.save(target);
  }
}
