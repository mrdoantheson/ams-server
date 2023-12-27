package ams.service.impl;

import ams.enums.TraineeClassStatus;
import ams.enums.TraineeStatus;
import ams.model.dto.TraineeDTO;
import ams.model.dto.mapper.TraineeMapper;
import ams.model.entity.ClassTrainee;
import ams.model.entity.Faculty;
import ams.model.entity.Trainee;
import ams.model.entity.University;
import ams.repository.TraineeRepository;
import ams.service.ClazzService;
import ams.service.FacultyService;
import ams.service.TraineeService;
import ams.service.UniversityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TraineeServiceImpl
        extends BaseServiceImpl<Trainee, Long, TraineeRepository>
        implements TraineeService {

    private final TraineeRepository traineeRepository;

    private ClazzService clazzService;

    private TraineeMapper traineeMapper;

    private UniversityService universityService;

    private FacultyService facultyService;


    @Override
    public boolean existEmailTrainee(String email) {
        Objects.requireNonNull(email);
        return traineeRepository.existsByEmailAndDeletedFalse(email);
    }

    @Override
    public boolean existPhoneTrainee(String phone) {
        Objects.requireNonNull(phone);

        return traineeRepository.existsByPhoneAndDeletedFalse(phone);
    }

    @Override
    public boolean existAccountTrainee(String account) {
        Objects.requireNonNull(account);
        return traineeRepository.existsByAccountAndDeletedFalse(account);
    }

    @Override
    public boolean existPhoneTraineeAndIdNot(String phone, Long id) {
        Objects.requireNonNull(phone);
        Objects.requireNonNull(id);

        return traineeRepository.existsByPhoneAndDeletedFalseAndIdNot(phone, id);
    }

    @Override
    public boolean existEmailTraineeAndIdNot(String email, Long id) {
        Objects.requireNonNull(email);
        Objects.requireNonNull(id);
        return traineeRepository.existsByEmailAndDeletedFalseAndIdNot(email, id);
    }

    @Override
    public boolean existAccountTraineeAndIdNot(String account, Long id) {
        Objects.requireNonNull(account);
        Objects.requireNonNull(id);
        return traineeRepository.existsByAccountAndDeletedFalseAndIdNot(account, id);
    }

    @Override
    public Trainee findTraineeByAccount(String account) {
        return traineeRepository.findByAccountAndDeletedFalse(account);
    }

    @Override
    public Optional<Trainee> findByAccount(String account) {
        return Optional.ofNullable(traineeRepository.findByAccountAndDeletedFalse(account));
    }

    @Override
    public void saveAllByTraineeDTO(List<TraineeDTO> traineeDTOList) {
        traineeRepository.saveAll(traineeDTOList.stream()
                .map(traineeDTO -> {
                    Trainee trainee = traineeMapper.toEntity(traineeDTO);

                    Optional<University> universityOpt = universityService.findUniByName(traineeDTO.getUniversity());
                    Optional<Faculty> facultyOpt = facultyService.findFacultyByName(traineeDTO.getFaculty());

                    if (universityOpt.isPresent()) {
                        trainee.setUniversity(universityOpt.get());
                    }

                    if (facultyOpt.isPresent()) {
                        trainee.setFaculty(facultyOpt.get());
                    }

                    if (!traineeDTO.getSalary()) {
                        trainee.setTpbAccount(null);
                        trainee.setContractStartDate(null);
                        trainee.setContractLength(null);
                        trainee.setAllowanceGroup(null);
                    }

                    return trainee;
                })
                .collect(Collectors.toList()));
    }

    @Override
    public void importTraineeToClass(List<TraineeDTO> traineeDTOList, Long clazzId) {
        traineeRepository.saveAll(traineeDTOList.stream()
                .map(traineeDTO -> {
                    Trainee trainee = traineeMapper.toEntity(traineeDTO);
                    trainee.setTraineeStatus(TraineeStatus.ENROLLED);

                    Set<ClassTrainee> classTraineeSet = new HashSet<>();
                    ClassTrainee classTrainee = new ClassTrainee();
                    classTrainee.setClazz(clazzService.findOne(clazzId));
                    classTrainee.setTrainee(trainee);
                    classTrainee.setTraineeClassStatus(TraineeClassStatus.ACTIVE);
                    classTraineeSet.add(classTrainee);

                    trainee.setClassTraineeSet(classTraineeSet);

                    Optional<University> universityOpt = universityService.findUniByName(traineeDTO.getUniversity());
                    Optional<Faculty> facultyOpt = facultyService.findFacultyByName(traineeDTO.getFaculty());

                    if (universityOpt.isPresent()) {
                        trainee.setUniversity(universityOpt.get());
                    }

                    if (facultyOpt.isPresent()) {
                        trainee.setFaculty(facultyOpt.get());
                    }

                    if (!traineeDTO.getSalary()) {
                        trainee.setTpbAccount(null);
                        trainee.setContractStartDate(null);
                        trainee.setContractLength(null);
                        trainee.setAllowanceGroup(null);
                    }

                    return trainee;
                })
                .collect(Collectors.toList()));
    }

}
