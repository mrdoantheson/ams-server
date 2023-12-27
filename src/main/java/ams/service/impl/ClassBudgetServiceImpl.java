package ams.service.impl;

import ams.model.entity.ClassBudget;
import ams.model.entity.Clazz;
import ams.repository.ClassBudgetRepository;
import ams.service.ClassBudgetService;
import ams.service.ClazzService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassBudgetServiceImpl extends BaseServiceImpl<ClassBudget, Long, ClassBudgetRepository>
        implements ClassBudgetService {

    private final ClassBudgetRepository classBudgetRepository;

    private final ClazzService clazzService;

    public ClassBudgetServiceImpl(ClassBudgetRepository classBudgetRepository, ClazzService clazzService) {
        this.classBudgetRepository = classBudgetRepository;
        this.clazzService = clazzService;
    }

    @Override
    public ClassBudget create(ClassBudget classBudget, Long aClazzId) {
        classBudget.setDeleted(false);
        classBudget.setCreatedDate(LocalDate.now());
        classBudget.setLastModifiedDate(LocalDateTime.now());
        Clazz clazz = clazzService.findOne(aClazzId);
        classBudget.setClazz(clazz);
        return classBudgetRepository.save(classBudget);
    }

    @Override
    public List<ClassBudget> findAllByClassId(Long id) {
        return classBudgetRepository.findByClazzIdAndDeletedFalse(id);
    }

}