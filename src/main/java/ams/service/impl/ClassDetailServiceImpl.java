package ams.service.impl;


import ams.model.entity.ClassDetail;
import ams.model.entity.Clazz;
import ams.repository.ClassDetailRepository;
import ams.service.ClassDetailService;
import ams.service.ClazzService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ClassDetailServiceImpl extends BaseServiceImpl<ClassDetail, Long, ClassDetailRepository> implements ClassDetailService {
    private final ClassDetailRepository classDetailRepository;
    private final ClazzService clazzService;

    public ClassDetailServiceImpl(ClassDetailRepository classDetailRepository, ClazzService clazzService) {
        this.classDetailRepository = classDetailRepository;
        this.clazzService = clazzService;
    }

    @Override
    public ClassDetail create(ClassDetail classDetail, Long id) {
        Clazz clazz = clazzService.findOne(id);
        classDetail.setClazz(clazz);
        classDetail.setDeleted(false);
        classDetail.setCreatedDate(LocalDate.now());
        classDetail.setLastModifiedDate(LocalDateTime.now());
        return classDetailRepository.save(classDetail);
    }

    @Override
    public Optional<ClassDetail> findOneByClassId(Long id) {
        return classDetailRepository.findByClazzIdAndDeletedFalse(id);
    }
}
