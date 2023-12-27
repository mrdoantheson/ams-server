package ams.service.impl;

import ams.enums.UserRole;
import ams.model.dto.EmployeeListDisplayDto;
import ams.model.entity.Account;
import ams.model.entity.Clazz;
import ams.repository.AccountRepository;
import ams.repository.ClazzRepository;
import ams.service.AccountService;
import ams.service.ClazzService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClazzServiceImpl extends BaseServiceImpl<Clazz, Long, ClazzRepository> implements ClazzService {

    private final ClazzRepository clazzRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


    public ClazzServiceImpl(ClazzRepository clazzRepository, AccountRepository accountRepository, AccountService accountService) {
        this.clazzRepository = clazzRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }


    @Override
    public Optional<Clazz> findByClassId(Long id) {
        return clazzRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public boolean isClassCodeExists(String classCode) {
        return clazzRepository.findByClassCodeAndDeletedFalse(classCode).isPresent();
    }

    @Override
    public List<EmployeeListDisplayDto> findAllByRole(UserRole role) {
        List<Account> roleList = accountService.findAllByRole(role);

        return roleList.stream()
                .map(account -> {
                    EmployeeListDisplayDto employeeListDisplayDto = new EmployeeListDisplayDto();
                    BeanUtils.copyProperties(account, employeeListDisplayDto);
//                    roleListDisplayDto.setAccount(account.getAccount());
                    return employeeListDisplayDto;
                })
                .toList();
    }

    @Override
    public Optional<Clazz> findClazzByCode(String clazzCode) {
        return clazzRepository.findByClassCodeAndDeletedFalse(clazzCode);
    }
}
