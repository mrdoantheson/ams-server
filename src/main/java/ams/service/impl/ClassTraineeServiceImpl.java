package ams.service.impl;

import ams.model.entity.ClassTrainee;
import ams.repository.ClassTraineeRepository;
import ams.service.ClassTraineeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClassTraineeServiceImpl
        extends BaseServiceImpl<ClassTrainee, Long, ClassTraineeRepository>
        implements ClassTraineeService {

    private final ClassTraineeRepository classTraineeRepository;

    @Override
    public Optional<ClassTrainee> findTraineeInClass(Long classId, Long traineeId) {
        Objects.requireNonNull(classId);
        Objects.requireNonNull(traineeId);
        return classTraineeRepository.findByClazzAndTraineeAndDeletedFalse(classId, traineeId);
    }

    @Override
    public Integer countTraineeInClass(Long classId) {
        Objects.requireNonNull(classId);
        return classTraineeRepository.countClassTraineeByClazz_IdAndDeletedFalse(classId);
    }

    @Override
    public Integer countClassOfTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId);
        return classTraineeRepository.countClassTraineeByTrainee_IdAndDeletedFalse(traineeId);
    }


}
