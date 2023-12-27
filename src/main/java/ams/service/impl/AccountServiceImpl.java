package ams.service.impl;

import ams.enums.UserRole;
import ams.model.entity.Account;
import ams.repository.AccountRepository;
import ams.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> findByAccount(String account) {
        return accountRepository.findByAccountIgnoreCase(account);
    }

    @Override
    public List<Account> findAllByRole(UserRole role) {
        return accountRepository.findAllByRole(role);
    }


    @Override
    public Optional<Account> findTraineeByAccount(String account) {
        return accountRepository.findByAccount(account);
    }

    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

}
