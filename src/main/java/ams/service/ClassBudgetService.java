package ams.service;

import ams.model.entity.ClassBudget;

import java.util.List;

public interface  ClassBudgetService extends BaseService<ClassBudget, Long> {

    ClassBudget create(ClassBudget classBudget, Long aClazzId);
    List<ClassBudget> findAllByClassId(Long id);
}
