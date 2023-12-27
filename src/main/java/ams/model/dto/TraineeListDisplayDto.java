package ams.model.dto;

import ams.enums.Gender;
import ams.enums.TraineeClassStatus;
import ams.enums.TraineeStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TraineeListDisplayDto {

    private Long id;

    private String account;

    private String fullName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String university;

    private String faculty;

    private String phone;

    private String email;

    private TraineeStatus traineeStatus;

    private TraineeClassStatus traineeClassStatus;

}
