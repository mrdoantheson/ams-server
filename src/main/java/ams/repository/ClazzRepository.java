package ams.repository;

import ams.model.entity.Clazz;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClazzRepository extends BaseRepository<Clazz,Long>{

    Optional<Clazz> findByIdAndDeletedFalse(Long id);
    Optional<Clazz> findByClassCodeAndDeletedFalse(String classCode);


}
