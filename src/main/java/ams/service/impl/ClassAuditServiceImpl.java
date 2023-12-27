package ams.service.impl;

import ams.model.entity.ClassAudit;
import ams.repository.ClassAuditRepository;
import ams.service.ClassAuditService;
import ams.service.ClazzService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ClassAuditServiceImpl extends BaseServiceImpl<ClassAudit, Long, ClassAuditRepository> implements ClassAuditService {

    private final ClassAuditRepository classAuditRepository;

    private final ClazzService clazzService;

    public ClassAuditServiceImpl(ClassAuditRepository classAuditRepository, ClazzService clazzService) {
        this.classAuditRepository = classAuditRepository;
        this.clazzService = clazzService;
    }


    @Override
    public ClassAudit create(ClassAudit classAudit, Long id) {
        classAudit.setDeleted(false);
        classAudit.setCreatedDate(LocalDate.now());
        classAudit.setLastModifiedDate(LocalDateTime.now());
        classAudit.setClazz(clazzService.findOne(id));
        return classAuditRepository.save(classAudit);
    }

    @Override
    public List<ClassAudit> findAllByClassId(Long id) {
        return classAuditRepository.findByClazzIdAndDeletedFalse(id);
    }

}
