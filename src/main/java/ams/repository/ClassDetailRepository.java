package ams.repository;

import ams.model.entity.ClassDetail;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClassDetailRepository extends BaseRepository<ClassDetail,Long>{
    Optional<ClassDetail> findByClazzIdAndDeletedFalse(Long id);
}
