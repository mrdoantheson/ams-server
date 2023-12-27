package ams.service;

import ams.model.entity.ClassDetail;

import java.util.Optional;

public interface ClassDetailService extends BaseService<ClassDetail, Long> {
    ClassDetail create(ClassDetail classDetail, Long id);
    Optional<ClassDetail> findOneByClassId(Long id);

}
