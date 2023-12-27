package ams.service;

import ams.model.dto.TraineeDTO;
import ams.model.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService
        extends BaseService<Trainee, Long> {
    boolean existEmailTrainee(String email);
    boolean existPhoneTrainee(String phone);
    boolean existAccountTrainee(String account);

    boolean existPhoneTraineeAndIdNot(String phone, Long id);
    boolean existEmailTraineeAndIdNot(String email, Long id);
    boolean existAccountTraineeAndIdNot(String account, Long id);

    Trainee findTraineeByAccount(String account);
    Optional<Trainee> findByAccount(String account);

    void saveAllByTraineeDTO(List<TraineeDTO> traineeDTOList);

    void importTraineeToClass(List<TraineeDTO> traineeDTOList, Long clazzId);


}
