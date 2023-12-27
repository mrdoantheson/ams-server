package ams.service;

import ams.enums.UserRole;
import ams.model.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<Account> findByAccount(String account);
    List<Account> findAllByRole(UserRole role);

    Optional<Account> findTraineeByAccount(String account);

    Account createAccount(Account account);


}
