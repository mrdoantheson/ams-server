package ams.model.dto.mapper.impl;


import ams.model.dto.TraineeDTO;
import ams.model.dto.mapper.TraineeMapper;
import ams.model.entity.Trainee;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapperImpl implements TraineeMapper {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TraineeDTO toDTO(Trainee trainee) {
        return modelMapper.map(trainee, TraineeDTO.class);
    }

    @Override
    public Trainee toEntity(TraineeDTO traineeDto) {

        return modelMapper.map(traineeDto, Trainee.class);

    }
}
