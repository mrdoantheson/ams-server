package ams.service;

import ams.model.entity.ClassAudit;

import java.util.List;

public interface ClassAuditService extends BaseService<ClassAudit, Long>{
    ClassAudit create(ClassAudit classAudit, Long id);

    List<ClassAudit> findAllByClassId(Long id);
}
