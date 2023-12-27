package ams.repository;

import ams.model.entity.ClassTrainee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author <a href="mailto:danghuyxd.3092@gmail.com"> HuyDD12
 * <p>
 * This interface represents the repository for Trainee entity.
 */

@Repository
public interface ClassTraineeRepository
        extends BaseRepository<ClassTrainee, Long> {

    @Query("SELECT ct FROM ClassTrainee ct WHERE ct.clazz.id = :classId AND ct.trainee.id = :traineeId AND ct.deleted = false")
    Optional<ClassTrainee> findByClazzAndTraineeAndDeletedFalse(@Param("classId") Long classId, @Param("traineeId") Long traineeId);


    Integer countClassTraineeByClazz_IdAndDeletedFalse(Long clazzId);

    Integer countClassTraineeByTrainee_IdAndDeletedFalse(Long traineeId);



}
