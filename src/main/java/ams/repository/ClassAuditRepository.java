package ams.repository;

import ams.model.entity.ClassAudit;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassAuditRepository extends BaseRepository<ClassAudit, Long>{
    List<ClassAudit> findByClazzIdAndDeletedFalse(Long id);
}
