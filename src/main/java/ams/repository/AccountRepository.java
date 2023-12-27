package ams.repository;

import ams.enums.UserRole;
import ams.model.entity.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, String> {

    Optional<Account> findByAccountIgnoreCase(String account);

    Optional<Account> findByAccount(String account);
    List<Account> findAllByRole(UserRole role);
}
