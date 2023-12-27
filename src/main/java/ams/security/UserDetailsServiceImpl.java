package ams.security;

import ams.constant.AppConstant;
import ams.enums.UserRole;
import ams.model.entity.Account;
import ams.service.AccountService;
import ams.service.TraineeService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountService accountService;
    private final TraineeService traineeService;

    public UserDetailsServiceImpl(AccountService accountService, TraineeService traineeService) {
        this.accountService = accountService;
        this.traineeService = traineeService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {


        Optional<Account> accountOptional = accountService.findByAccount(username);
        if (accountOptional.isEmpty()) {
            throw new UsernameNotFoundException("Account: \"" + username + "\" is not exist");
        }

        Account account = accountOptional.get();

        if (account.getRole().equals(UserRole.TRAINEE)) {
            if (!traineeService.existAccountTrainee(username)) {
                throw new UsernameNotFoundException("Account: \"" + username + "\" is not exist");
            }
        }

        List<GrantedAuthority> roles = Collections.singletonList(
                new SimpleGrantedAuthority(AppConstant.USER_ROLE_PREFIX + account.getRole().name())
        );

        return new User(account.getAccount(),
                account.getPassword(), roles);
    }
}
