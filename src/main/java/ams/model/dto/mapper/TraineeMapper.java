package ams.model.dto.mapper;

import ams.model.dto.TraineeDTO;
import ams.model.entity.Trainee;


public interface TraineeMapper {

    Trainee toEntity(TraineeDTO traineeDto);

    TraineeDTO toDTO(Trainee trainee);

}
