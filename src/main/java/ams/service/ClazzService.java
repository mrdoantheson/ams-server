package ams.service;

import ams.enums.UserRole;
import ams.enums.UserRole;
import ams.model.dto.EmployeeListDisplayDto;
import ams.model.entity.Account;
import ams.model.entity.Clazz;
import ams.model.dto.EmployeeListDisplayDto;

import java.util.List;
import java.util.Optional;

public interface ClazzService extends BaseService <Clazz, Long> {
    Optional<Clazz> findByClassId(Long id);
    boolean isClassCodeExists(String classCode);
    List<EmployeeListDisplayDto> findAllByRole(UserRole role);

    Optional<Clazz> findClazzByCode(String clazzCode);

}
