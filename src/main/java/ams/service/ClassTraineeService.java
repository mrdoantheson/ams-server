package ams.service;

import ams.model.entity.ClassTrainee;

import java.util.Optional;

public interface ClassTraineeService
        extends BaseService<ClassTrainee, Long> {


    Optional<ClassTrainee> findTraineeInClass(Long classId, Long traineeId);

    Integer countTraineeInClass(Long classId);
    Integer countClassOfTrainee(Long traineeId);

}
