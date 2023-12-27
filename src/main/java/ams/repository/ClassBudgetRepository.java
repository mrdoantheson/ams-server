package ams.repository;

import ams.model.entity.ClassBudget;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassBudgetRepository extends BaseRepository<ClassBudget, Long> {
    List<ClassBudget> findByClazzIdAndDeletedFalse(Long id);

}
